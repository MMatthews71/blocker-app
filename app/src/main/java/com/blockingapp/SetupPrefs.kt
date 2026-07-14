package com.blockingapp

import android.content.Context

/**
 * Flags for setup steps we can't detect programmatically and therefore rely on
 * the user to confirm. The background-usage bucket ("Unrestricted / Optimized /
 * Restricted") has no public read API, so its card is dismissed manually.
 */
object SetupPrefs {

    private const val PREFS = "setup_prefs"
    private const val KEY_BG_RESTRICTION_DONE = "bg_restriction_done"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isBackgroundRestrictionHandled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_BG_RESTRICTION_DONE, false)

    fun setBackgroundRestrictionHandled(context: Context, handled: Boolean) {
        prefs(context).edit().putBoolean(KEY_BG_RESTRICTION_DONE, handled).apply()
    }
}
