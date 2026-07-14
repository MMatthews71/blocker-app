package com.blockingapp.service

import android.content.ComponentName
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import android.widget.Toast
import com.blockingapp.BlockerRepository
import com.blockingapp.MusicSettings
import com.blockingapp.data.BlockedApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Limits — rather than fully blocks — music from "music-limited" apps.
 *
 * The user may listen for up to the configurable [MusicSettings] budget per day
 * (shared across all music-limited apps). Browsing those apps is unaffected —
 * only active playback burns the budget. Once it's spent, playback is paused
 * whenever it resumes for the rest of the day; the budget resets at midnight.
 *
 * Time is measured by polling the system's active media sessions rather than by
 * reacting to notification posts, which don't fire continuously during playback.
 */
class MediaBlockerNotificationListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())
    private val repository by lazy { BlockerRepository.getInstance(this) }

    private val mediaSessionManager by lazy { getSystemService(MediaSessionManager::class.java) }
    private val selfComponent by lazy {
        ComponentName(this, MediaBlockerNotificationListenerService::class.java)
    }

    @Volatile private var musicPackages: Set<String> = emptySet()

    // Best-known total for today, kept in sync with the DB via each accrual's
    // return value (which is daily-reset aware).
    @Volatile private var musicMsUsedToday: Long = 0L

    // elapsedRealtime of the last accrual while playing; 0 means "not currently
    // accruing" (nothing was playing at the previous tick).
    private var lastAccrualAt: Long = 0L

    private var lastLimitToastAt: Long = 0L

    private val sessionsChangedListener =
        MediaSessionManager.OnActiveSessionsChangedListener { enforceTick() }

    private val ticker = object : Runnable {
        override fun run() {
            try {
                enforceTick()
            } catch (t: Throwable) {
                // Never let the limiter crash the listener.
            }
            mainHandler.postDelayed(this, TICK_MS)
        }
    }

    override fun onListenerConnected() {
        instance = this
        scope.launch {
            musicPackages = repository.getMusicModePackages().toSet()
            musicMsUsedToday = repository.getMusicMsUsedToday()
            repository.blockedAppsFlow
                .onEach { apps ->
                    musicPackages = apps
                        .filter { it.mode == BlockedApp.MODE_MUSIC }
                        .map { it.packageName }
                        .toSet()
                }
                .launchIn(scope)
        }

        // Guard against onListenerConnected firing more than once (reconnect).
        runCatching {
            mediaSessionManager.removeOnActiveSessionsChangedListener(sessionsChangedListener)
            mediaSessionManager.addOnActiveSessionsChangedListener(sessionsChangedListener, selfComponent)
        }
        mainHandler.removeCallbacks(ticker)
        mainHandler.postDelayed(ticker, TICK_MS)
    }

    /** Active media sessions belonging to music-limited apps that are playing. */
    private fun playingMusicControllers(): List<MediaController> = runCatching {
        mediaSessionManager.getActiveSessions(selfComponent)
            .filter { it.packageName in musicPackages }
            .filter { it.playbackState?.state == PlaybackState.STATE_PLAYING }
    }.getOrDefault(emptyList())

    /**
     * One accrual/enforcement pass. Runs on the main thread; the DB write and any
     * pause happen off it. [lastAccrualAt] is only touched here, so it needs no
     * synchronization.
     */
    private fun enforceTick() {
        val playing = playingMusicControllers()
        if (playing.isEmpty()) {
            lastAccrualAt = 0L
            return
        }

        val now = SystemClock.elapsedRealtime()
        val wasAccruing = lastAccrualAt != 0L
        val delta = if (wasAccruing) now - lastAccrualAt else 0L
        lastAccrualAt = now

        scope.launch {
            // On the first tick of a playback run, refresh from the DB (handles the
            // midnight reset) instead of adding a delta.
            val used = if (wasAccruing) {
                repository.addMusicListeningTime(delta)
            } else {
                repository.getMusicMsUsedToday()
            }
            musicMsUsedToday = used
            val budgetMs = MusicSettings.budgetMs(this@MediaBlockerNotificationListenerService)
            if (used >= budgetMs) {
                pauseNow()
                notifyLimitReached()
            }
        }
    }

    private fun pauseNow() {
        // Re-query so we pause whatever is playing right now, not a stale list.
        playingMusicControllers().forEach { it.transportControls.pause() }
    }

    private fun notifyLimitReached() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastLimitToastAt < LIMIT_TOAST_INTERVAL_MS) return
        lastLimitToastAt = now
        mainHandler.post {
            Toast.makeText(
                this,
                "Daily music limit reached. It resets tomorrow.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        mainHandler.removeCallbacks(ticker)
        runCatching {
            mediaSessionManager.removeOnActiveSessionsChangedListener(sessionsChangedListener)
        }
        scope.cancel()
        instance = null
        super.onDestroy()
    }

    companion object {
        private const val TICK_MS = 15_000L
        private const val LIMIT_TOAST_INTERVAL_MS = 5L * 60 * 1000

        @Volatile var instance: MediaBlockerNotificationListenerService? = null
    }
}
