package com.blockingapp

import android.content.Context

/**
 * The 24-hour "wait before you can delete the app" gate.
 *
 * Stored in SharedPreferences (not the Room daily-state table) on purpose: the
 * daily state resets every calendar day, but this timer must survive across days
 * — otherwise the wait would silently disappear at midnight.
 *
 * States (see [state]):
 *  - LOCKED  : uninstall is blocked; user must request it.
 *  - PENDING : user requested; still counting down the 24 hours.
 *  - OPEN    : the wait elapsed; there's a short window to actually uninstall
 *              before it re-locks itself.
 */
object UninstallGate {

    enum class State { LOCKED, PENDING, OPEN }

    /** How long the user must wait after requesting an uninstall. */
    const val WAIT_MS = 24L * 60 * 60 * 1000

    /**
     * After the wait elapses, how long the uninstall window stays open before it
     * re-locks. Keeps protection from silently turning off forever if the user
     * requests an uninstall but then changes their mind.
     */
    const val WINDOW_MS = 60L * 60 * 1000

    private const val PREFS = "uninstall_gate"
    private const val KEY_UNLOCK_AT = "unlock_at"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Timestamp when the wait completes, or 0 if no request is active. */
    fun unlockAt(context: Context): Long = prefs(context).getLong(KEY_UNLOCK_AT, 0L)

    fun state(context: Context, now: Long = System.currentTimeMillis()): State {
        val unlockAt = unlockAt(context)
        return when {
            unlockAt == 0L -> State.LOCKED
            now < unlockAt -> State.PENDING
            now < unlockAt + WINDOW_MS -> State.OPEN
            else -> State.LOCKED // window expired without an uninstall — re-lock
        }
    }

    /** Whether the accessibility service should block routes to uninstall/deactivate. */
    fun shouldGuard(context: Context, now: Long = System.currentTimeMillis()): Boolean =
        state(context, now) != State.OPEN

    /** Start the 24-hour countdown. */
    fun requestUninstall(context: Context) {
        prefs(context).edit().putLong(KEY_UNLOCK_AT, System.currentTimeMillis() + WAIT_MS).apply()
    }

    /** Cancel a pending request (or clear an open/expired window) and re-lock. */
    fun cancel(context: Context) {
        prefs(context).edit().putLong(KEY_UNLOCK_AT, 0L).apply()
    }
}
