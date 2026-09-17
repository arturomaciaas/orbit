package com.orbit.blocker.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.orbit.blocker.data.model.FocusOutcome;
import com.orbit.blocker.data.model.FocusSessionRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FocusSessionDao_Impl implements FocusSessionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FocusSessionRecord> __insertionAdapterOfFocusSessionRecord;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public FocusSessionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFocusSessionRecord = new EntityInsertionAdapter<FocusSessionRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `focus_sessions` (`id`,`startedAt`,`endedAt`,`plannedDurationMillis`,`outcome`,`blockedPackageCount`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FocusSessionRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getStartedAt());
        statement.bindLong(3, entity.getEndedAt());
        statement.bindLong(4, entity.getPlannedDurationMillis());
        final String _tmp = __converters.focusOutcomeToString(entity.getOutcome());
        statement.bindString(5, _tmp);
        statement.bindLong(6, entity.getBlockedPackageCount());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM focus_sessions";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final FocusSessionRecord record,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFocusSessionRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<FocusSessionRecord> records,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFocusSessionRecord.insert(records);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FocusSessionRecord>> observeAll() {
    final String _sql = "SELECT * FROM focus_sessions ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"focus_sessions"}, new Callable<List<FocusSessionRecord>>() {
      @Override
      @NonNull
      public List<FocusSessionRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfPlannedDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedDurationMillis");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final int _cursorIndexOfBlockedPackageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "blockedPackageCount");
          final List<FocusSessionRecord> _result = new ArrayList<FocusSessionRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FocusSessionRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpEndedAt;
            _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            final long _tmpPlannedDurationMillis;
            _tmpPlannedDurationMillis = _cursor.getLong(_cursorIndexOfPlannedDurationMillis);
            final FocusOutcome _tmpOutcome;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfOutcome);
            _tmpOutcome = __converters.stringToFocusOutcome(_tmp);
            final int _tmpBlockedPackageCount;
            _tmpBlockedPackageCount = _cursor.getInt(_cursorIndexOfBlockedPackageCount);
            _item = new FocusSessionRecord(_tmpId,_tmpStartedAt,_tmpEndedAt,_tmpPlannedDurationMillis,_tmpOutcome,_tmpBlockedPackageCount);
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
  public Flow<Integer> observeCompletedCount() {
    final String _sql = "SELECT COUNT(*) FROM focus_sessions WHERE outcome = 'COMPLETED'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"focus_sessions"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getAll(final Continuation<? super List<FocusSessionRecord>> $completion) {
    final String _sql = "SELECT * FROM focus_sessions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FocusSessionRecord>>() {
      @Override
      @NonNull
      public List<FocusSessionRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfPlannedDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "plannedDurationMillis");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final int _cursorIndexOfBlockedPackageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "blockedPackageCount");
          final List<FocusSessionRecord> _result = new ArrayList<FocusSessionRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FocusSessionRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final long _tmpEndedAt;
            _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            final long _tmpPlannedDurationMillis;
            _tmpPlannedDurationMillis = _cursor.getLong(_cursorIndexOfPlannedDurationMillis);
            final FocusOutcome _tmpOutcome;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfOutcome);
            _tmpOutcome = __converters.stringToFocusOutcome(_tmp);
            final int _tmpBlockedPackageCount;
            _tmpBlockedPackageCount = _cursor.getInt(_cursorIndexOfBlockedPackageCount);
            _item = new FocusSessionRecord(_tmpId,_tmpStartedAt,_tmpEndedAt,_tmpPlannedDurationMillis,_tmpOutcome,_tmpBlockedPackageCount);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
