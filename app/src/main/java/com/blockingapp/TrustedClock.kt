package com.blockingapp

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.provider.Settings

/**
 * A wall-clock time source the user can't wind forward or back from Settings.
 *
 * [System.currentTimeMillis] follows whatever the user sets the phone's clock to,
 * so setting it to 11:59 PM and waiting a minute used to trigger the daily reset
 * (fresh breaks, pending removals applied, music budget cleared).
 *
 * Instead, we take one wall-clock reading as an *anchor* and then advance it with
 * [SystemClock.elapsedRealtime], a monotonic counter that changing the date/time
 * doesn't affect. The anchor is taken from:
 *  1. Network time (NTP/NITZ) on Android 13+, when available — re-checked
 *     periodically so a clock that was wrong at install time gets corrected.
 *  2. Otherwise the device clock, but only once per boot (elapsedRealtime resets
 *     on reboot) and never earlier than the latest time we've already seen.
 */
object TrustedClock {

    private const val PREFS = "trusted_clock"
    private const val KEY_ANCHOR_WALL = "anchor_wall"
    private const val KEY_ANCHOR_ELAPSED = "anchor_elapsed"
    private const val KEY_ANCHOR_BOOT = "anchor_boot"
    private const val KEY_LAST_SEEN = "last_seen"

    private const val NETWORK_RECHECK_MS = 10L * 60 * 1000
    private const val LAST_SEEN_PERSIST_MS = 60L * 1000

    private class Anchor(val wall: Long, val elapsed: Long, val boot: Int)

    private var anchor: Anchor? = null
    private var loaded = false
    private var lastNetworkCheck: Long? = null
    private var persistedLastSeen = 0L

    /** Current time in epoch millis, resistant to manual clock changes. */
    fun now(context: Context): Long = synchronized(this) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!loaded) {
            loaded = true
            persistedLastSeen = prefs.getLong(KEY_LAST_SEEN, 0L)
            if (prefs.contains(KEY_ANCHOR_WALL)) {
                anchor = Anchor(
                    prefs.getLong(KEY_ANCHOR_WALL, 0L),
                    prefs.getLong(KEY_ANCHOR_ELAPSED, 0L),
                    prefs.getInt(KEY_ANCHOR_BOOT, -1)
                )
            }
        }

        val elapsed = SystemClock.elapsedRealtime()
        val boot = bootCount(context)
        var a = anchor?.takeIf { isSameBoot(it, boot, elapsed) }

        // Network time is outside the user's control, so it may (re)anchor us at any time.
        val sinceCheck = lastNetworkCheck?.let { elapsed - it }
        if (a == null || sinceCheck == null || sinceCheck >= NETWORK_RECHECK_MS) {
            lastNetworkCheck = elapsed
            networkTimeMillis()?.let { a = Anchor(it, SystemClock.elapsedRealtime(), boot) }
        }

        // New boot (or first run) and no network time: the device clock is all we
        // have, but don't let it move backwards past what we've already observed.
        val resolved = a ?: Anchor(
            maxOf(System.currentTimeMillis(), persistedLastSeen),
            elapsed,
            boot
        )

        val now = resolved.wall + (elapsed - resolved.elapsed)
        val anchorChanged = resolved !== anchor
        anchor = resolved

        if (anchorChanged || now - persistedLastSeen >= LAST_SEEN_PERSIST_MS || now < persistedLastSeen) {
            persistedLastSeen = now
            prefs.edit().apply {
                if (anchorChanged) {
                    putLong(KEY_ANCHOR_WALL, resolved.wall)
                    putLong(KEY_ANCHOR_ELAPSED, resolved.elapsed)
                    putInt(KEY_ANCHOR_BOOT, resolved.boot)
                }
                putLong(KEY_LAST_SEEN, now)
            }.apply()
        }
        now
    }

    private fun isSameBoot(a: Anchor, boot: Int, elapsed: Long): Boolean {
        // elapsedRealtime restarts from 0 on reboot, so a smaller value means a new boot.
        if (elapsed < a.elapsed) return false
        return boot == -1 || a.boot == -1 || boot == a.boot
    }

    private fun bootCount(context: Context): Int =
        runCatching {
            Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
        }.getOrDefault(-1)

    private fun networkTimeMillis(): Long? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
        // Throws DateTimeException when the device has no network time fix yet.
        return runCatching { SystemClock.currentNetworkTimeClock().millis() }.getOrNull()
    }
}
