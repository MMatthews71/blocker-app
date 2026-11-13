package com.blockingapp

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.blockingapp.admin.FocusDeviceAdminReceiver
import com.blockingapp.data.BlockedApp
import com.blockingapp.databinding.ActivityMainBinding
import com.blockingapp.ui.AppInfo
import com.blockingapp.ui.AppListAdapter
import com.blockingapp.ui.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: AppListAdapter

    private val dpm by lazy { getSystemService(DevicePolicyManager::class.java) }
    private val adminComponent by lazy { FocusDeviceAdminReceiver.componentName(this) }
    private var uninstallTicker: Job? = null

    // POST_NOTIFICATIONS runtime request (Android 13+). If the user permanently
    // denied it, the system dialog no longer appears, so fall back to settings.
    private val requestNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                openAppNotificationSettings()
            }
            refreshNotificationsStatus()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AppListAdapter { app -> onAppTapped(app) }
        binding.rvApps.adapter = adapter
        binding.rvApps.layoutManager = LinearLayoutManager(this)
        binding.rvApps.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.btnEnableAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        binding.btnBatteryOptimization.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
            )
        }

        binding.btnEnableNotifications.setOnClickListener { enableNotifications() }

        binding.btnEnableOverlay.setOnClickListener {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        }

        binding.btnBatteryCard.setOnClickListener {
            startActivity(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
            )
        }

        binding.btnOpenAppDetails.setOnClickListener { openAppDetailsSettings() }

        binding.btnBackgroundRestrictionDone.setOnClickListener {
            SetupPrefs.setBackgroundRestrictionHandled(this, true)
            refreshBackgroundRestrictionStatus()
        }

        lifecycleScope.launch {
            viewModel.apps.collect { apps -> adapter.submitList(apps) }
        }

        viewModel.dailyState.observe(this) { updateStatusText() }
    }

    private fun updateStatusText() {
        val state = viewModel.dailyState.value ?: return
        val used = state.breaksUsed
        val remaining = BlockerRepository.MAX_BREAKS - used
        val breakMins = BlockerRepository.BREAK_DURATION_MS / 60000
        binding.tvBreakStatus.text =
            "$used / ${BlockerRepository.MAX_BREAKS} breaks used today  •  $remaining remaining  •  ${breakMins}-min each"
    }

    private fun onAppTapped(app: AppInfo) {
        when {
            app.isPendingRemoval -> showReblockDialog(app)
            app.isBlocked -> showUnblockConfirmation(app)
            app.blockedFeature != null -> showUnblockConfirmation(app)
            else -> showAddModeDialog(app)
        }
    }

    private fun showAddModeDialog(app: AppInfo) {
        // Apps with a blockable section (e.g. Snapchat Discover) list that option first.
        val feature = FeatureBlocks.forPackage(app.packageName)
        val choices = buildList {
            if (feature != null) {
                add(feature.optionLabel to BlockedApp.MODE_FEATURE)
            }
            add("Block app entirely" to BlockedApp.MODE_BLOCK)
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(app.name)
            .setItems(choices.map { it.first }.toTypedArray()) { _, which ->
                viewModel.addApp(app.packageName, choices[which].second)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUnblockConfirmation(app: AppInfo) {
        val feature = app.blockedFeature
        val title = when {
            feature != null -> "Unblock ${app.name} $feature?"
            else -> "Unblock ${app.name}?"
        }
        val pendingNote = when {
            feature != null -> "($feature stays blocked until tomorrow)"
            else -> "(app stays blocked until tomorrow)"
        }
        val finalNote = when {
            feature != null -> "${app.name} $feature will stay blocked until tomorrow."
            else -> "${app.name} will stay blocked until tomorrow."
        }

        var countdownJob: Job? = null

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage("Confirm in $UNBLOCK_DELAY_SECONDS seconds…")
            .setNegativeButton("Cancel") { _, _ -> countdownJob?.cancel() }
            .setPositiveButton("Confirm") { _, _ -> viewModel.toggleBlock(app.packageName) }
            .create()

        dialog.show()

        // Keep the confirm button disabled until the countdown finishes.
        val confirmBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        confirmBtn.isEnabled = false

        countdownJob = lifecycleScope.launch {
            for (i in UNBLOCK_DELAY_SECONDS downTo 1) {
                if (!dialog.isShowing) return@launch
                dialog.setMessage("Confirm in $i seconds… $pendingNote")
                delay(1000)
            }
            if (dialog.isShowing) {
                confirmBtn.isEnabled = true
                dialog.setMessage(finalNote)
            }
        }

        dialog.setOnDismissListener { countdownJob?.cancel() }
    }

    private fun showReblockDialog(app: AppInfo) {
        MaterialAlertDialogBuilder(this)
            .setTitle("${app.name}'s limit ends tomorrow")
            .setMessage("Do you want to re-enable it immediately instead?")
            .setNegativeButton("Keep scheduled") { _, _ -> }
            .setPositiveButton("Re-enable now") { _, _ -> viewModel.toggleBlock(app.packageName) }
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshAccessibilityStatus()
        refreshNotificationsStatus()
        refreshOverlayStatus()
        refreshBatteryStatus()
        refreshBackgroundRestrictionStatus()
        refreshUninstallCard()
        startUninstallTicker()
        viewModel.loadApps(packageManager)
    }

    override fun onPause() {
        super.onPause()
        uninstallTicker?.cancel()
        uninstallTicker = null
    }

    /** Re-render the uninstall card once a second while a countdown/window is live. */
    private fun startUninstallTicker() {
        uninstallTicker?.cancel()
        uninstallTicker = lifecycleScope.launch {
            while (true) {
                delay(1000)
                refreshUninstallCard()
            }
        }
    }

    private fun refreshUninstallCard() {
        val adminActive = dpm.isAdminActive(adminComponent)
        val title = binding.tvUninstallTitle
        val body = binding.tvUninstallBody
        val primary = binding.btnUninstallPrimary
        val secondary = binding.btnUninstallSecondary

        // Default: secondary hidden; each branch re-enables it when needed.
        secondary.visibility = View.GONE

        if (!adminActive) {
            title.text = "Uninstall protection is off"
            body.text = "Turn this on so deleting Focus Blocker takes a 24-hour wait. " +
                "That wait kills the impulse to delete the app just to reach a blocked one."
            primary.text = "Turn on protection"
            primary.setOnClickListener { requestDeviceAdmin() }
            return
        }

        when (UninstallGate.state(this)) {
            UninstallGate.State.LOCKED -> {
                title.text = "Uninstall protection is on"
                body.text = "To delete Focus Blocker you must request it below and wait 24 hours. " +
                    "There's no instant off switch — that's the point."
                primary.text = "Request uninstall"
                primary.setOnClickListener { confirmRequestUninstall() }
            }
            UninstallGate.State.PENDING -> {
                val remaining = UninstallGate.unlockAt(this) - TrustedClock.now(this)
                title.text = "Uninstall unlocks soon"
                body.text = "Unlocks in ${formatDuration(remaining)}. " +
                    "You can cancel and stay protected."
                primary.text = "Cancel request"
                primary.setOnClickListener {
                    UninstallGate.cancel(this)
                    refreshUninstallCard()
                }
            }
            UninstallGate.State.OPEN -> {
                val remaining = UninstallGate.unlockAt(this) + UninstallGate.WINDOW_MS - TrustedClock.now(this)
                title.text = "Uninstall unlocked"
                body.text = "You can remove the app for the next ${formatDuration(remaining)}, " +
                    "then protection re-locks."
                primary.text = "Uninstall now"
                primary.setOnClickListener { uninstallNow() }
                secondary.visibility = View.VISIBLE
                secondary.text = "Turn off protection"
                secondary.setOnClickListener {
                    dpm.removeActiveAdmin(adminComponent)
                    UninstallGate.cancel(this)
                    refreshUninstallCard()
                }
            }
        }
    }

    private fun requestDeviceAdmin() {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                // Keep this text free of the guard's escape keywords (see
                // AppBlockerAccessibilityService.ESCAPE_KEYWORDS), otherwise the
                // guard would bounce the user off this very activation screen.
                "Device admin lets Focus Blocker enforce a 24-hour waiting period " +
                    "before it can be taken off your phone."
            )
        }
        startActivity(intent)
    }

    private fun confirmRequestUninstall() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Request uninstall?")
            .setMessage(
                "You'll be able to delete Focus Blocker in 24 hours. Until then it stays " +
                    "active and blocking. Give yourself a day to reconsider."
            )
            .setNegativeButton("Never mind", null)
            .setPositiveButton("Start 24-hour wait") { _, _ ->
                UninstallGate.requestUninstall(this)
                refreshUninstallCard()
            }
            .show()
    }

    private fun uninstallNow() {
        // Release the device-admin lock first, otherwise Android still refuses
        // the uninstall.
        if (dpm.isAdminActive(adminComponent)) dpm.removeActiveAdmin(adminComponent)
        UninstallGate.cancel(this)
        startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")))
    }

    private fun formatDuration(millis: Long): String {
        val clamped = millis.coerceAtLeast(0)
        val hours = TimeUnit.MILLISECONDS.toHours(clamped)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(clamped) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(clamped) % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    private fun refreshAccessibilityStatus() {
        val enabled = isAccessibilityServiceEnabled()
        binding.cardSetup.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun refreshNotificationsStatus() {
        val enabled = NotificationManagerCompat.from(this).areNotificationsEnabled()
        binding.cardNotifications.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun refreshOverlayStatus() {
        val granted = Settings.canDrawOverlays(this)
        binding.cardOverlay.visibility = if (granted) View.GONE else View.VISIBLE
    }

    private fun enableNotifications() {
        val needsRuntimePermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsRuntimePermission) {
            requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Pre-13, or permission granted but notifications toggled off in settings.
            openAppNotificationSettings()
        }
    }

    private fun openAppNotificationSettings() {
        startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        )
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val target = "$packageName/com.blockingapp.service.AppBlockerAccessibilityService"
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(":").any { it.equals(target, ignoreCase = true) }
    }

    private fun refreshBatteryStatus() {
        val pm = getSystemService(PowerManager::class.java)
        val exempted = pm.isIgnoringBatteryOptimizations(packageName)
        binding.cardBattery.visibility = if (exempted) View.GONE else View.VISIBLE
    }

    // The background-usage bucket has no public read API, so this card is
    // manual-confirm: it stays until the user taps "I've done this".
    private fun refreshBackgroundRestrictionStatus() {
        val handled = SetupPrefs.isBackgroundRestrictionHandled(this)
        binding.cardBackgroundRestriction.visibility = if (handled) View.GONE else View.VISIBLE
    }

    /** Opens this app's details page, where the Battery > Unrestricted control lives. */
    private fun openAppDetailsSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            )
        )
    }

    companion object {
        private const val UNBLOCK_DELAY_SECONDS = 30
    }
}
