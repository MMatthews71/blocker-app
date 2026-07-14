package com.blockingapp.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile BlockedAppDao _blockedAppDao;

  private volatile DailyFocusStateDao _dailyFocusStateDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `blocked_apps` (`packageName` TEXT NOT NULL, `pendingRemoval` INTEGER NOT NULL, `mode` TEXT NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_focus_state` (`id` INTEGER NOT NULL, `dateString` TEXT NOT NULL, `breaksUsed` INTEGER NOT NULL, `breakEndsAt` INTEGER NOT NULL, `musicMsUsed` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b9ce3d8832859fb44ccd2cdef92ec271')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `blocked_apps`");
        db.execSQL("DROP TABLE IF EXISTS `daily_focus_state`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsBlockedApps = new HashMap<String, TableInfo.Column>(3);
        _columnsBlockedApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockedApps.put("pendingRemoval", new TableInfo.Column("pendingRemoval", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockedApps.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBlockedApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBlockedApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBlockedApps = new TableInfo("blocked_apps", _columnsBlockedApps, _foreignKeysBlockedApps, _indicesBlockedApps);
        final TableInfo _existingBlockedApps = TableInfo.read(db, "blocked_apps");
        if (!_infoBlockedApps.equals(_existingBlockedApps)) {
          return new RoomOpenHelper.ValidationResult(false, "blocked_apps(com.blockingapp.data.BlockedApp).\n"
                  + " Expected:\n" + _infoBlockedApps + "\n"
                  + " Found:\n" + _existingBlockedApps);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyFocusState = new HashMap<String, TableInfo.Column>(5);
        _columnsDailyFocusState.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFocusState.put("dateString", new TableInfo.Column("dateString", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFocusState.put("breaksUsed", new TableInfo.Column("breaksUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFocusState.put("breakEndsAt", new TableInfo.Column("breakEndsAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFocusState.put("musicMsUsed", new TableInfo.Column("musicMsUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyFocusState = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyFocusState = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyFocusState = new TableInfo("daily_focus_state", _columnsDailyFocusState, _foreignKeysDailyFocusState, _indicesDailyFocusState);
        final TableInfo _existingDailyFocusState = TableInfo.read(db, "daily_focus_state");
        if (!_infoDailyFocusState.equals(_existingDailyFocusState)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_focus_state(com.blockingapp.data.DailyFocusState).\n"
                  + " Expected:\n" + _infoDailyFocusState + "\n"
                  + " Found:\n" + _existingDailyFocusState);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b9ce3d8832859fb44ccd2cdef92ec271", "78229c219c5730d66341ba6d7764c36c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "blocked_apps","daily_focus_state");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `blocked_apps`");
      _db.execSQL("DELETE FROM `daily_focus_state`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(BlockedAppDao.class, BlockedAppDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyFocusStateDao.class, DailyFocusStateDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public BlockedAppDao blockedAppDao() {
    if (_blockedAppDao != null) {
      return _blockedAppDao;
    } else {
      synchronized(this) {
        if(_blockedAppDao == null) {
          _blockedAppDao = new BlockedAppDao_Impl(this);
        }
        return _blockedAppDao;
      }
    }
  }

  @Override
  public DailyFocusStateDao dailyFocusStateDao() {
    if (_dailyFocusStateDao != null) {
      return _dailyFocusStateDao;
    } else {
      synchronized(this) {
        if(_dailyFocusStateDao == null) {
          _dailyFocusStateDao = new DailyFocusStateDao_Impl(this);
        }
        return _dailyFocusStateDao;
      }
    }
  }
}
