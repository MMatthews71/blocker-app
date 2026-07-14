package com.blockingapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Query("SELECT * FROM blocked_apps")
    fun getAllFlow(): Flow<List<BlockedApp>>

    @Query("SELECT * FROM blocked_apps")
    suspend fun getAll(): List<BlockedApp>

    @Query("SELECT packageName FROM blocked_apps")
    suspend fun getAllPackageNames(): List<String>

    /** Packages that should trigger the full block screen (music-limited apps stay browsable). */
    @Query("SELECT packageName FROM blocked_apps WHERE mode = 'BLOCK'")
    suspend fun getBlockModePackageNames(): List<String>

    /** Packages whose music playback is limited to the daily budget. */
    @Query("SELECT packageName FROM blocked_apps WHERE mode = 'MUSIC'")
    suspend fun getMusicModePackageNames(): List<String>

    @Query("SELECT * FROM blocked_apps WHERE packageName = :pkg LIMIT 1")
    suspend fun get(pkg: String): BlockedApp?

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE packageName = :pkg")
    suspend fun isBlocked(pkg: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: BlockedApp)

    @Update
    suspend fun update(app: BlockedApp)

    @Delete
    suspend fun delete(app: BlockedApp)

    @Query("DELETE FROM blocked_apps WHERE pendingRemoval = 1")
    suspend fun deletePendingRemovals()
}
