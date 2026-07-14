package com.blockingapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyFocusStateDao {

    @Query("SELECT * FROM daily_focus_state WHERE id = 1")
    suspend fun get(): DailyFocusState?

    @Query("SELECT * FROM daily_focus_state WHERE id = 1")
    fun getFlow(): Flow<DailyFocusState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: DailyFocusState)

    /** Atomic increment so frequent music updates don't clobber the break columns. */
    @Query("UPDATE daily_focus_state SET musicMsUsed = musicMsUsed + :deltaMs WHERE id = 1")
    suspend fun addMusicMs(deltaMs: Long)

    @Query("SELECT musicMsUsed FROM daily_focus_state WHERE id = 1")
    suspend fun getMusicMs(): Long?
}
