package com.blockingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BlockedApp::class, DailyFocusState::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun dailyFocusStateDao(): DailyFocusStateDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE blocked_apps ADD COLUMN pendingRemoval INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE blocked_apps ADD COLUMN mode TEXT NOT NULL DEFAULT 'BLOCK'")
                db.execSQL("ALTER TABLE daily_focus_state ADD COLUMN musicMsUsed INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE daily_focus_state ADD COLUMN resetsAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        /**
         * Rebuilds both tables into the current shape, keeping every column that
         * still exists and filling the rest with defaults. Covers:
         *  - v4/v5/v6: some installs came from an unpublished branch with an
         *    unknown layout; rebuild rather than crash or wipe the block list.
         *  - v7 -> v8: music limiting was removed — drop music-limited entries
         *    (they become unblocked) and the musicMsUsed column.
         */
        private fun rebuildMigration(from: Int) = object : Migration(from, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Music limiting no longer exists: those apps are simply unblocked.
                // Done before the rebuild, which turns unknown modes into BLOCK.
                if (hasColumn(db, "blocked_apps", "mode")) {
                    db.execSQL("DELETE FROM `blocked_apps` WHERE `mode` = 'MUSIC'")
                }
                rebuildTable(
                    db, "blocked_apps",
                    "(`packageName` TEXT NOT NULL, `pendingRemoval` INTEGER NOT NULL, " +
                        "`mode` TEXT NOT NULL, PRIMARY KEY(`packageName`))",
                    listOf(
                        "packageName" to null, // required: rows without it are dropped
                        "pendingRemoval" to "0",
                        // Unknown modes from the other branch become a full block,
                        // so nothing silently stops being blocked.
                        "mode" to "'BLOCK'"
                    ),
                    transforms = mapOf(
                        "mode" to "CASE WHEN `mode` IN ('BLOCK','FEATURE') THEN `mode` ELSE 'BLOCK' END"
                    )
                )
                rebuildTable(
                    db, "daily_focus_state",
                    "(`id` INTEGER NOT NULL, `dateString` TEXT NOT NULL, `breaksUsed` INTEGER NOT NULL, " +
                        "`breakEndsAt` INTEGER NOT NULL, `resetsAt` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))",
                    listOf(
                        "id" to "1",
                        "dateString" to "''",
                        "breaksUsed" to "0",
                        "breakEndsAt" to "0",
                        "resetsAt" to "0"
                    )
                )
            }
        }

        private fun hasColumn(db: SupportSQLiteDatabase, table: String, column: String): Boolean =
            columnsOf(db, table).contains(column)

        private fun columnsOf(db: SupportSQLiteDatabase, table: String): Set<String> {
            val names = HashSet<String>()
            db.query("PRAGMA table_info(`$table`)").use { c ->
                val nameIdx = c.getColumnIndex("name")
                while (c.moveToNext()) names.add(c.getString(nameIdx))
            }
            return names
        }

        /**
         * Recreates [table] with [columnsSql], copying over each of [columns] that
         * exists in the old table (null or missing -> the paired default SQL).
         * If copying fails for any reason, the table starts empty rather than
         * leaving the app unable to open its database.
         */
        private fun rebuildTable(
            db: SupportSQLiteDatabase,
            table: String,
            columnsSql: String,
            columns: List<Pair<String, String?>>,
            transforms: Map<String, String> = emptyMap()
        ) {
            val existing = columnsOf(db, table)

            val newTable = "${table}_new"
            db.execSQL("DROP TABLE IF EXISTS `$newTable`")
            db.execSQL("CREATE TABLE `$newTable` $columnsSql")

            val required = columns.filter { it.second == null }.map { it.first }
            if (existing.isNotEmpty() && existing.containsAll(required)) {
                try {
                    val names = columns.joinToString(", ") { "`${it.first}`" }
                    val values = columns.joinToString(", ") { (name, default) ->
                        when {
                            name !in existing -> default!!
                            default == null -> "`$name`"
                            else -> "COALESCE(${transforms[name] ?: "`$name`"}, $default)"
                        }
                    }
                    val notNull = required.joinToString(" AND ") { "`$it` IS NOT NULL" }
                    val where = if (notNull.isEmpty()) "" else " WHERE $notNull"
                    db.execSQL("INSERT OR IGNORE INTO `$newTable` ($names) SELECT $values FROM `$table`$where")
                } catch (t: Throwable) {
                    db.execSQL("DELETE FROM `$newTable`")
                }
            }

            db.execSQL("DROP TABLE IF EXISTS `$table`")
            db.execSQL("ALTER TABLE `$newTable` RENAME TO `$table`")
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focus_db"
                )
                    .addMigrations(
                        MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                        rebuildMigration(4), rebuildMigration(5), rebuildMigration(6),
                        rebuildMigration(7)
                    )
                    // Installing an older build over a newer one must never leave
                    // the blocker in a crash loop; start fresh instead.
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
