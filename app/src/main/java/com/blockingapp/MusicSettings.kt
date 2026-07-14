package com.blockingapp

import android.content.Context

/**
 * User-configurable daily music-listening allowance for music-limited apps.
 * Stored in SharedPreferences so the media service can read it cheaply.
 */
object MusicSettings {

    const val DEFAULT_BUDGET_MIN = 60
    const val MAX_BUDGET_MIN = 180
    const val STEP_MIN = 15

    private const val PREFS = "music_settings"
    private const val KEY_BUDGET_MIN = "budget_min"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun budgetMinutes(context: Context): Int =
        prefs(context).getInt(KEY_BUDGET_MIN, DEFAULT_BUDGET_MIN)
            .coerceIn(0, MAX_BUDGET_MIN)

    fun setBudgetMinutes(context: Context, minutes: Int) {
        prefs(context).edit()
            .putInt(KEY_BUDGET_MIN, minutes.coerceIn(0, MAX_BUDGET_MIN))
            .apply()
    }

    fun budgetMs(context: Context): Long = budgetMinutes(context) * 60_000L
}
