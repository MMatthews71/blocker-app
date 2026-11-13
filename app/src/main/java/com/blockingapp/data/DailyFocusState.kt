package com.blockingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_focus_state")
data class DailyFocusState(
    @PrimaryKey val id: Int = 1,
    val dateString: String,
    val breaksUsed: Int = 0,
    val breakEndsAt: Long = 0L,
    /** Milliseconds of music listened to today from music-limited apps. */
    val musicMsUsed: Long = 0L,
    /**
     * Trusted epoch millis at which this state expires and a new day starts.
     * Fixed when the day begins, so later clock or time-zone changes can't pull
     * it closer. 0 = legacy row created before this column existed.
     */
    val resetsAt: Long = 0L
)
