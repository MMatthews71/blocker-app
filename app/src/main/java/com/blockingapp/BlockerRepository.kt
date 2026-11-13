package com.blockingapp

import android.content.Context
import com.blockingapp.data.AppDatabase
import com.blockingapp.data.BlockedApp
import com.blockingapp.data.DailyFocusState
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId

class BlockerRepository private constructor(
    private val context: Context,
    db: AppDatabase
) {

    private val blockedAppDao = db.blockedAppDao()
    private val stateDao = db.dailyFocusStateDao()

    val blockedAppsFlow: Flow<List<BlockedApp>> = blockedAppDao.getAllFlow()
    val dailyStateFlow: Flow<DailyFocusState?> = stateDao.getFlow()

    /** Tamper-resistant "now" — see [TrustedClock]. */
    fun now(): Long = TrustedClock.now(context)

    private fun dateString(at: Long): String =
        Instant.ofEpochMilli(at).atZone(ZoneId.systemDefault()).toLocalDate().toString()

    private fun nextMidnight(at: Long): Long {
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(at).atZone(zone).toLocalDate()
            .plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
    }

    /**
     * When the day that starts now should end: the next local midnight, but never
     * sooner than [MIN_DAY_MS] after the previous reset. The floor stops switching
     * time zones (e.g. to one where it's already 11 PM) from producing extra
     * resets, while still letting real travel and DST land close to midnight.
     */
    private fun nextResetAt(now: Long, previousResetAt: Long): Long =
        maxOf(nextMidnight(now), previousResetAt + MIN_DAY_MS)

    suspend fun getDailyState(): DailyFocusState {
        val now = now()
        val existing = stateDao.get()
        val expired = when {
            existing == null -> true
            existing.resetsAt == 0L -> existing.dateString != dateString(now) // legacy row
            else -> now >= existing.resetsAt
        }
        if (existing == null || expired) {
            blockedAppDao.deletePendingRemovals()
            val fresh = DailyFocusState(
                dateString = dateString(now),
                resetsAt = nextResetAt(now, existing?.resetsAt ?: 0L)
            )
            stateDao.upsert(fresh)
            return fresh
        }
        if (existing.resetsAt == 0L) {
            // Legacy row from today: pin its reset moment now.
            return existing.copy(resetsAt = nextMidnight(now)).also { stateDao.upsert(it) }
        }
        return existing
    }

    suspend fun isOnBreak(): Boolean {
        val state = getDailyState()
        return state.breakEndsAt > now()
    }

    suspend fun canTakeBreak(): Boolean {
        val state = getDailyState()
        return state.breaksUsed < MAX_BREAKS
    }

    /** Returns the breakEndsAt timestamp, or -1 if no breaks remain. */
    suspend fun startBreak(): Long {
        val state = getDailyState()
        if (state.breaksUsed >= MAX_BREAKS) return -1L
        val breakEndsAt = now() + BREAK_DURATION_MS
        stateDao.upsert(state.copy(breaksUsed = state.breaksUsed + 1, breakEndsAt = breakEndsAt))
        return breakEndsAt
    }

    suspend fun getAllBlockedPackages(): List<String> = blockedAppDao.getAllPackageNames()

    /** Packages that trigger the full block screen — excludes music-limited apps. */
    suspend fun getBlockModePackages(): List<String> = blockedAppDao.getBlockModePackageNames()

    /** Packages whose music playback is capped by the daily budget. */
    suspend fun getMusicModePackages(): List<String> = blockedAppDao.getMusicModePackageNames()

    suspend fun getAllBlockedApps(): List<BlockedApp> = blockedAppDao.getAll()

    suspend fun isBlocked(packageName: String): Boolean = blockedAppDao.isBlocked(packageName) > 0

    /** Add an app in a specific mode, or clear a pending removal if it already exists. */
    suspend fun addApp(packageName: String, mode: String) {
        val existing = blockedAppDao.get(packageName)
        if (existing == null) {
            blockedAppDao.insert(BlockedApp(packageName, mode = mode))
        } else {
            blockedAppDao.update(existing.copy(pendingRemoval = false, mode = mode))
        }
    }

    /**
     * Tap toggle for an existing entry: a scheduled removal is undone (re-enabled),
     * otherwise removal is scheduled for the next day (kept until midnight so the
     * user can't instantly undo their own limit).
     */
    suspend fun toggleBlock(packageName: String) {
        val existing = blockedAppDao.get(packageName)
        when {
            existing == null -> blockedAppDao.insert(BlockedApp(packageName))
            existing.pendingRemoval -> blockedAppDao.update(existing.copy(pendingRemoval = false))
            else -> blockedAppDao.update(existing.copy(pendingRemoval = true))
        }
    }

    suspend fun getMusicMsUsedToday(): Long = getDailyState().musicMsUsed

    /** Add elapsed listening time to today's music budget; returns the new total. */
    suspend fun addMusicListeningTime(deltaMs: Long): Long {
        getDailyState() // ensure today's row exists (handles the midnight reset)
        if (deltaMs > 0) stateDao.addMusicMs(deltaMs)
        return stateDao.getMusicMs() ?: 0L
    }

    companion object {
        const val MAX_BREAKS = 2
        const val BREAK_DURATION_MS = 15L * 60 * 1000
        private const val MIN_DAY_MS = 20L * 60 * 60 * 1000

        @Volatile private var INSTANCE: BlockerRepository? = null

        fun getInstance(context: Context): BlockerRepository =
            INSTANCE ?: synchronized(this) {
                BlockerRepository(
                    context.applicationContext,
                    AppDatabase.getInstance(context)
                ).also { INSTANCE = it }
            }
    }
}
