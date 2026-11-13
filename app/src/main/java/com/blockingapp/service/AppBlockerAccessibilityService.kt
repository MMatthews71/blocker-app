package com.blockingapp.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationManager
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.blockingapp.BlockerRepository
import com.blockingapp.FeatureBlocks
import com.blockingapp.FocusApp
import com.blockingapp.R
import com.blockingapp.TrustedClock
import com.blockingapp.UninstallGate
import com.blockingapp.data.BlockedApp
import com.blockingapp.ui.BlockActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppBlockerAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())

    private val repository by lazy { BlockerRepository.getInstance(this) }
    private val powerManager by lazy { getSystemService(PowerManager::class.java) }

    private var blockedPackages: Set<String> = emptySet()

    // Apps where only one section is blocked (e.g. Snapchat -> Discover).
    @Volatile private var featurePackages: Map<String, FeatureBlocks.Feature> = emptyMap()
    private val appLabels = HashMap<String, String>()
    private var lastFeatureScanAt = 0L
    private var lastFeatureBlockAt = 0L

    // Polling safety net. Window-state events only fire when a window first
    // appears — not while scrolling inside an already-open app. If the process
    // was killed during idle and rebound mid-scroll, no event fires until you
    // navigate elsewhere. Polling the live foreground window catches it fast.
    private val pollRunnable = object : Runnable {
        override fun run() {
            // Never let an exception here kill the service — it would take the
            // whole blocker process down and force a slow system rebind.
            try {
                if (powerManager.isInteractive) {
                    checkAndBlockForeground()
                    updateHeartbeat()
                }
            } catch (t: Throwable) {
                // Swallow and keep polling.
            }
            mainHandler.postDelayed(this, POLL_INTERVAL_MS)
        }
    }

    // In-memory break state to avoid a DB round-trip on every window event.
    // Set immediately when a break starts so the next window-state event
    // (triggered when the blocked app becomes visible again) sees the break.
    @Volatile var inMemoryBreakEndsAt: Long = 0L

    private var lastForegroundPackage: String? = null

    // App label ("Focus Blocker"), lowercased, used to recognize the settings /
    // package-installer screens that talk about *our* app.
    private var appLabel: String = ""

    // Debounce so we bounce out of an escape screen at most once per interval,
    // rather than firing HOME repeatedly while the screen is up.
    private var lastBounceAt: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        appLabel = runCatching {
            applicationInfo.loadLabel(packageManager).toString().lowercase()
        }.getOrDefault("focus blocker")

        startForegroundService(Intent(this, BlockerForegroundService::class.java))

        // Load blocked packages and break state before subscribing to changes,
        // so no window events slip through while the Flow emits its first value.
        scope.launch {
            blockedPackages = repository.getBlockModePackages().toSet()
            featurePackages = featureMap(repository.getAllBlockedApps())
            inMemoryBreakEndsAt = repository.getDailyState().breakEndsAt

            // Check immediately, then start the polling safety net.
            mainHandler.post { checkAndBlockForeground() }
            mainHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS)

            // Only full-block apps trigger the block screen; section-blocked apps
            // stay usable and are handled by checkFeatureBlock().
            repository.blockedAppsFlow
                .onEach { apps ->
                    blockedPackages = apps
                        .filter { it.mode == com.blockingapp.data.BlockedApp.MODE_BLOCK }
                        .map { it.packageName }
                        .toSet()
                    featurePackages = featureMap(apps)
                }
                .launchIn(scope)
        }
    }

    private fun featureMap(apps: List<BlockedApp>): Map<String, FeatureBlocks.Feature> =
        apps.filter { it.mode == BlockedApp.MODE_FEATURE }
            .mapNotNull { app -> FeatureBlocks.forPackage(app.packageName)?.let { app.packageName to it } }
            .toMap()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName) return // never block our own app

        // Section blocks: switching tabs inside an app (e.g. to Snapchat's
        // Discover) only fires content-changed events, not window-state ones.
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (featurePackages.containsKey(pkg)) {
                val now = android.os.SystemClock.uptimeMillis()
                if (now - lastFeatureScanAt >= FEATURE_SCAN_THROTTLE_MS) {
                    lastFeatureScanAt = now
                    checkFeatureBlock(pkg)
                }
            }
            return
        }
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        // Uninstall protection: bounce the user away from the screens they'd use
        // to delete the app or turn off protection, unless the 24-hour wait has
        // elapsed. Wrapped so a traversal failure can never kill the service.
        try {
            if (isEscapeRoutePackage(pkg) && UninstallGate.shouldGuard(this) && isEscapeScreen()) {
                bounceFromEscapeScreen()
                return
            }
        } catch (t: Throwable) {
            // Swallow — never let uninstall-guarding take down the blocker.
        }

        if (pkg == "android" || pkg == "com.android.systemui") return

        lastForegroundPackage = pkg

        if (isOnBreakFast()) return
        if (featurePackages.containsKey(pkg)) {
            checkFeatureBlock(pkg)
            return
        }
        if (!blockedPackages.contains(pkg)) return

        mainHandler.post { launchBlockActivity() }
    }

    /**
     * If [pkg] is showing its blocked section right now, move it off that screen
     * and show the block screen. Everything else in the app is left alone.
     */
    private fun checkFeatureBlock(pkg: String) {
        try {
            if (isOnBreakFast()) return
            val feature = featurePackages[pkg] ?: return
            val root = rootInActiveWindow ?: return
            if (root.packageName?.toString() != pkg) return
            if (!screenShowsLabel(root, feature.screenLabels)) return

            val now = android.os.SystemClock.uptimeMillis()
            if (now - lastFeatureBlockAt < FEATURE_BLOCK_DEBOUNCE_MS) return
            lastFeatureBlockAt = now

            // Step the app back out of the section first, so closing the block
            // screen lands the user somewhere usable instead of right back on it.
            // Launch after a short delay so this BACK can't close the block screen.
            performGlobalAction(GLOBAL_ACTION_BACK)
            mainHandler.postDelayed({
                launchBlockActivity(feature.name, appLabel(pkg))
            }, FEATURE_BLOCK_LAUNCH_DELAY_MS)
        } catch (t: Throwable) {
            // Never let a traversal failure take down the blocker.
        }
    }

    /**
     * True when a visible element's text or content description is exactly one of
     * [labels]. Unselected tabs/buttons are skipped: a button that *leads* to the
     * section isn't the section being open.
     */
    private fun screenShowsLabel(root: AccessibilityNodeInfo, labels: Set<String>): Boolean {
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var visited = 0
        while (queue.isNotEmpty() && visited < MAX_FEATURE_NODES_SCANNED) {
            val node = queue.removeFirst()
            visited++
            if (node.isVisibleToUser) {
                val text = node.text?.toString()?.trim()?.lowercase()
                val desc = node.contentDescription?.toString()?.trim()?.lowercase()
                val matches = (text != null && text in labels) || (desc != null && desc in labels)
                if (matches && !(node.isClickable && !node.isSelected)) {
                    return true
                }
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return false
    }

    private fun appLabel(pkg: String): String = appLabels.getOrPut(pkg) {
        runCatching {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(pkg, 0)).toString()
        }.getOrDefault(pkg)
    }

    override fun onInterrupt() {}

    private fun isEscapeRoutePackage(pkg: String): Boolean =
        pkg == "com.android.settings" || pkg.contains("packageinstaller")

    /**
     * True when the current screen is one of the "delete / disable Focus Blocker"
     * routes:
     *  - app-info page (Uninstall / Force stop) and the uninstall confirmation,
     *  - device-admin *deactivation* screen,
     *  - the accessibility "Turn off / Stop Focus Blocker?" confirmation dialog
     *    that Android always shows before disabling our service.
     *
     * Heuristic: the screen mentions our app label AND an escape keyword. We
     * require both to avoid bouncing the user out of unrelated Settings pages,
     * and we deliberately match the *action* words ("deactivate", "turn off",
     * "stop") rather than page words like "device admin"/"accessibility", so the
     * admin activation screen and the general Accessibility list still work — we
     * only intervene at the final confirmation step.
     */
    private fun isEscapeScreen(): Boolean {
        val text = collectScreenText(rootInActiveWindow) ?: return false
        if (!text.contains(appLabel)) return false
        return ESCAPE_KEYWORDS.any { text.contains(it) }
    }

    /** Depth/size-bounded traversal collecting lowercased text from the window. */
    private fun collectScreenText(root: AccessibilityNodeInfo?): String? {
        root ?: return null
        val sb = StringBuilder()
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var visited = 0
        while (queue.isNotEmpty() && visited < MAX_NODES_SCANNED) {
            val node = queue.removeFirst()
            visited++
            node.text?.let { sb.append(it).append(' ') }
            node.contentDescription?.let { sb.append(it).append(' ') }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return sb.toString().lowercase()
    }

    private fun bounceFromEscapeScreen() {
        val now = System.currentTimeMillis()
        if (now - lastBounceAt < BOUNCE_DEBOUNCE_MS) return
        lastBounceAt = now
        performGlobalAction(GLOBAL_ACTION_HOME)
        mainHandler.post {
            Toast.makeText(
                this,
                "Request the uninstall in Focus Blocker and wait 24 hours first.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkAndBlockForeground() {
        val live = rootInActiveWindow?.packageName?.toString()
        if (live != null && live != packageName) lastForegroundPackage = live

        if (live != null && featurePackages.containsKey(live)) {
            checkFeatureBlock(live)
            return
        }

        val candidates = setOfNotNull(live, lastForegroundPackage)
        for (pkg in candidates) {
            if (pkg == packageName || pkg == "android" || pkg == "com.android.systemui") continue
            if (!isOnBreakFast() && blockedPackages.contains(pkg)) {
                launchBlockActivity()
                return
            }
        }
    }

    // Diagnostic heartbeat: stamps the ongoing notification with the time of the
    // last live foreground check. If, after a blocking failure, this time is frozen
    // at when the phone went idle, the process was killed (an OS/battery issue).
    // If the time is current but blocking lagged, it's a detection bug in our code.
    private fun updateHeartbeat() {
        val now = heartbeatFormat.format(Date())
        val notification = NotificationCompat.Builder(this, FocusApp.CHANNEL_ID)
            .setContentTitle("Focus Blocker active")
            .setContentText("Last check: $now")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        getSystemService(NotificationManager::class.java)
            .notify(BlockerForegroundService.NOTIFICATION_ID, notification)
    }

    private fun isOnBreakFast(): Boolean =
        TrustedClock.now(this) < inMemoryBreakEndsAt

    /** Full block by default; pass [feature] to block just one section of [appLabel]. */
    private fun launchBlockActivity(feature: String? = null, appLabel: String? = null) {
        val intent = Intent(this, BlockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (feature != null) {
                putExtra(BlockActivity.EXTRA_FEATURE, feature)
                putExtra(BlockActivity.EXTRA_APP_LABEL, appLabel)
            }
        }
        startActivity(intent)
    }

    /** Called by BlockActivity the moment a break is confirmed, before finish(). */
    fun notifyBreakStarted(breakEndsAt: Long) {
        inMemoryBreakEndsAt = breakEndsAt

        // When the break expires, re-check whatever is in the foreground.
        val delay = breakEndsAt - TrustedClock.now(this)
        mainHandler.removeCallbacksAndMessages(BREAK_END_TOKEN)
        mainHandler.postAtTime({
            val pkg = lastForegroundPackage ?: return@postAtTime
            if (pkg == packageName) return@postAtTime
            if (featurePackages.containsKey(pkg)) {
                checkFeatureBlock(pkg)
                return@postAtTime
            }
            if (!isOnBreakFast() && blockedPackages.contains(pkg)) {
                launchBlockActivity()
            }
        }, BREAK_END_TOKEN, android.os.SystemClock.uptimeMillis() + delay + 100)
    }

    override fun onDestroy() {
        scope.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        instance = null
        stopService(Intent(this, BlockerForegroundService::class.java))
        super.onDestroy()
    }

    private val heartbeatFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    companion object {
        private val BREAK_END_TOKEN = Any()
        private const val POLL_INTERVAL_MS = 1000L

        // Uninstall-guard tuning.
        private const val MAX_NODES_SCANNED = 400
        private const val BOUNCE_DEBOUNCE_MS = 1500L

        // Section-block tuning.
        private const val FEATURE_SCAN_THROTTLE_MS = 300L
        private const val FEATURE_BLOCK_DEBOUNCE_MS = 2000L
        private const val FEATURE_BLOCK_LAUNCH_DELAY_MS = 250L
        private const val MAX_FEATURE_NODES_SCANNED = 1500
        // Action words on the final confirm step of each escape route:
        //  uninstall / force stop -> app-info page & uninstall dialog
        //  deactivate             -> device-admin deactivation screen
        //  turn off / stop        -> accessibility disable confirmation dialog
        private val ESCAPE_KEYWORDS =
            listOf("uninstall", "force stop", "deactivate", "turn off", "stop")

        @Volatile var instance: AppBlockerAccessibilityService? = null
    }
}
