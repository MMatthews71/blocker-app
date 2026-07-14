package com.blockingapp.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DailyFocusStateDao_Impl implements DailyFocusStateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyFocusState> __insertionAdapterOfDailyFocusState;

  private final SharedSQLiteStatement __preparedStmtOfAddMusicMs;

  public DailyFocusStateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyFocusState = new EntityInsertionAdapter<DailyFocusState>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_focus_state` (`id`,`dateString`,`breaksUsed`,`breakEndsAt`,`musicMsUsed`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyFocusState entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getDateString() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDateString());
        }
        statement.bindLong(3, entity.getBreaksUsed());
        statement.bindLong(4, entity.getBreakEndsAt());
        statement.bindLong(5, entity.getMusicMsUsed());
      }
    };
    this.__preparedStmtOfAddMusicMs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE daily_focus_state SET musicMsUsed = musicMsUsed + ? WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final DailyFocusState state, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyFocusState.insert(state);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object addMusicMs(final long deltaMs, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddMusicMs.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deltaMs);
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
          __preparedStmtOfAddMusicMs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final Continuation<? super DailyFocusState> $completion) {
    final String _sql = "SELECT * FROM daily_focus_state WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyFocusState>() {
      @Override
      @Nullable
      public DailyFocusState call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfBreaksUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "breaksUsed");
          final int _cursorIndexOfBreakEndsAt = CursorUtil.getColumnIndexOrThrow(_cursor, "breakEndsAt");
          final int _cursorIndexOfMusicMsUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "musicMsUsed");
          final DailyFocusState _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDateString;
            if (_cursor.isNull(_cursorIndexOfDateString)) {
              _tmpDateString = null;
            } else {
              _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            }
            final int _tmpBreaksUsed;
            _tmpBreaksUsed = _cursor.getInt(_cursorIndexOfBreaksUsed);
            final long _tmpBreakEndsAt;
            _tmpBreakEndsAt = _cursor.getLong(_cursorIndexOfBreakEndsAt);
            final long _tmpMusicMsUsed;
            _tmpMusicMsUsed = _cursor.getLong(_cursorIndexOfMusicMsUsed);
            _result = new DailyFocusState(_tmpId,_tmpDateString,_tmpBreaksUsed,_tmpBreakEndsAt,_tmpMusicMsUsed);
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
  public Flow<DailyFocusState> getFlow() {
    final String _sql = "SELECT * FROM daily_focus_state WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_focus_state"}, new Callable<DailyFocusState>() {
      @Override
      @Nullable
      public DailyFocusState call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final int _cursorIndexOfBreaksUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "breaksUsed");
          final int _cursorIndexOfBreakEndsAt = CursorUtil.getColumnIndexOrThrow(_cursor, "breakEndsAt");
          final int _cursorIndexOfMusicMsUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "musicMsUsed");
          final DailyFocusState _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDateString;
            if (_cursor.isNull(_cursorIndexOfDateString)) {
              _tmpDateString = null;
            } else {
              _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            }
            final int _tmpBreaksUsed;
            _tmpBreaksUsed = _cursor.getInt(_cursorIndexOfBreaksUsed);
            final long _tmpBreakEndsAt;
            _tmpBreakEndsAt = _cursor.getLong(_cursorIndexOfBreakEndsAt);
            final long _tmpMusicMsUsed;
            _tmpMusicMsUsed = _cursor.getLong(_cursorIndexOfMusicMsUsed);
            _result = new DailyFocusState(_tmpId,_tmpDateString,_tmpBreaksUsed,_tmpBreakEndsAt,_tmpMusicMsUsed);
          } else {
            _result = null;
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
  public Object getMusicMs(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT musicMsUsed FROM daily_focus_state WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getLong(0);
            }
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
