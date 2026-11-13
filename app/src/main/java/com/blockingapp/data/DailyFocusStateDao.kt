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
}
