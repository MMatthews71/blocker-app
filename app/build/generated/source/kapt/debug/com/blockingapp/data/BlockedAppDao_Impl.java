package com.blockingapp.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class BlockedAppDao_Impl implements BlockedAppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BlockedApp> __insertionAdapterOfBlockedApp;

  private final EntityDeletionOrUpdateAdapter<BlockedApp> __deletionAdapterOfBlockedApp;

  private final EntityDeletionOrUpdateAdapter<BlockedApp> __updateAdapterOfBlockedApp;

  private final SharedSQLiteStatement __preparedStmtOfDeletePendingRemovals;

  public BlockedAppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBlockedApp = new EntityInsertionAdapter<BlockedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `blocked_apps` (`packageName`,`pendingRemoval`,`mode`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlockedApp entity) {
        if (entity.getPackageName() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getPackageName());
        }
        final int _tmp = entity.getPendingRemoval() ? 1 : 0;
        statement.bindLong(2, _tmp);
        if (entity.getMode() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMode());
        }
      }
    };
    this.__deletionAdapterOfBlockedApp = new EntityDeletionOrUpdateAdapter<BlockedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `blocked_apps` WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlockedApp entity) {
        if (entity.getPackageName() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getPackageName());
        }
      }
    };
    this.__updateAdapterOfBlockedApp = new EntityDeletionOrUpdateAdapter<BlockedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `blocked_apps` SET `packageName` = ?,`pendingRemoval` = ?,`mode` = ? WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlockedApp entity) {
        if (entity.getPackageName() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getPackageName());
        }
        final int _tmp = entity.getPendingRemoval() ? 1 : 0;
        statement.bindLong(2, _tmp);
        if (entity.getMode() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getMode());
        }
        if (entity.getPackageName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPackageName());
        }
      }
    };
    this.__preparedStmtOfDeletePendingRemovals = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM blocked_apps WHERE pendingRemoval = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BlockedApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBlockedApp.insert(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final BlockedApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBlockedApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BlockedApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBlockedApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePendingRemovals(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePendingRemovals.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePendingRemovals.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BlockedApp>> getAllFlow() {
    final String _sql = "SELECT * FROM blocked_apps";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"blocked_apps"}, new Callable<List<BlockedApp>>() {
      @Override
      @NonNull
      public List<BlockedApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfPendingRemoval = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingRemoval");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final List<BlockedApp> _result = new ArrayList<BlockedApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BlockedApp _item;
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final boolean _tmpPendingRemoval;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPendingRemoval);
            _tmpPendingRemoval = _tmp != 0;
            final String _tmpMode;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmpMode = null;
            } else {
              _tmpMode = _cursor.getString(_cursorIndexOfMode);
            }
            _item = new BlockedApp(_tmpPackageName,_tmpPendingRemoval,_tmpMode);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<BlockedApp>> $completion) {
    final String _sql = "SELECT * FROM blocked_apps";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BlockedApp>>() {
      @Override
      @NonNull
      public List<BlockedApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfPendingRemoval = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingRemoval");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final List<BlockedApp> _result = new ArrayList<BlockedApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BlockedApp _item;
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final boolean _tmpPendingRemoval;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPendingRemoval);
            _tmpPendingRemoval = _tmp != 0;
            final String _tmpMode;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmpMode = null;
            } else {
              _tmpMode = _cursor.getString(_cursorIndexOfMode);
            }
            _item = new BlockedApp(_tmpPackageName,_tmpPendingRemoval,_tmpMode);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPackageNames(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT packageName FROM blocked_apps";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getBlockModePackageNames(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT packageName FROM blocked_apps WHERE mode = 'BLOCK'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMusicModePackageNames(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT packageName FROM blocked_apps WHERE mode = 'MUSIC'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getString(0);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final String pkg, final Continuation<? super BlockedApp> $completion) {
    final String _sql = "SELECT * FROM blocked_apps WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (pkg == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, pkg);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BlockedApp>() {
      @Override
      @Nullable
      public BlockedApp call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfPendingRemoval = CursorUtil.getColumnIndexOrThrow(_cursor, "pendingRemoval");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final BlockedApp _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPackageName;
            if (_cursor.isNull(_cursorIndexOfPackageName)) {
              _tmpPackageName = null;
            } else {
              _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            }
            final boolean _tmpPendingRemoval;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfPendingRemoval);
            _tmpPendingRemoval = _tmp != 0;
            final String _tmpMode;
            if (_cursor.isNull(_cursorIndexOfMode)) {
              _tmpMode = null;
            } else {
              _tmpMode = _cursor.getString(_cursorIndexOfMode);
            }
            _result = new BlockedApp(_tmpPackageName,_tmpPendingRemoval,_tmpMode);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object isBlocked(final String pkg, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM blocked_apps WHERE packageName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (pkg == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, pkg);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
