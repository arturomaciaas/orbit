package com.orbit.blocker.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.orbit.blocker.data.model.GalaxyProgress;
import com.orbit.blocker.data.model.GalaxyStage;
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
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GalaxyProgressDao_Impl implements GalaxyProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GalaxyProgress> __insertionAdapterOfGalaxyProgress;

  private final Converters __converters = new Converters();

  private final EntityUpsertionAdapter<GalaxyProgress> __upsertionAdapterOfGalaxyProgress;

  public GalaxyProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGalaxyProgress = new EntityInsertionAdapter<GalaxyProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `galaxy_progress` (`id`,`stage`,`progress`,`totalSessionsCompleted`,`currentStreakDays`,`longestStreakDays`,`lastSessionCompletedAt`,`meteorStrikes`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GalaxyProgress entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.galaxyStageToString(entity.getStage());
        statement.bindString(2, _tmp);
        statement.bindDouble(3, entity.getProgress());
        statement.bindLong(4, entity.getTotalSessionsCompleted());
        statement.bindLong(5, entity.getCurrentStreakDays());
        statement.bindLong(6, entity.getLongestStreakDays());
        if (entity.getLastSessionCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastSessionCompletedAt());
        }
        statement.bindLong(8, entity.getMeteorStrikes());
      }
    };
    this.__upsertionAdapterOfGalaxyProgress = new EntityUpsertionAdapter<GalaxyProgress>(new EntityInsertionAdapter<GalaxyProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `galaxy_progress` (`id`,`stage`,`progress`,`totalSessionsCompleted`,`currentStreakDays`,`longestStreakDays`,`lastSessionCompletedAt`,`meteorStrikes`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GalaxyProgress entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.galaxyStageToString(entity.getStage());
        statement.bindString(2, _tmp);
        statement.bindDouble(3, entity.getProgress());
        statement.bindLong(4, entity.getTotalSessionsCompleted());
        statement.bindLong(5, entity.getCurrentStreakDays());
        statement.bindLong(6, entity.getLongestStreakDays());
        if (entity.getLastSessionCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastSessionCompletedAt());
        }
        statement.bindLong(8, entity.getMeteorStrikes());
      }
    }, new EntityDeletionOrUpdateAdapter<GalaxyProgress>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `galaxy_progress` SET `id` = ?,`stage` = ?,`progress` = ?,`totalSessionsCompleted` = ?,`currentStreakDays` = ?,`longestStreakDays` = ?,`lastSessionCompletedAt` = ?,`meteorStrikes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GalaxyProgress entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.galaxyStageToString(entity.getStage());
        statement.bindString(2, _tmp);
        statement.bindDouble(3, entity.getProgress());
        statement.bindLong(4, entity.getTotalSessionsCompleted());
        statement.bindLong(5, entity.getCurrentStreakDays());
        statement.bindLong(6, entity.getLongestStreakDays());
        if (entity.getLastSessionCompletedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastSessionCompletedAt());
        }
        statement.bindLong(8, entity.getMeteorStrikes());
        statement.bindLong(9, entity.getId());
      }
    });
  }

  @Override
  public Object insertIfAbsent(final GalaxyProgress progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGalaxyProgress.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final GalaxyProgress progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfGalaxyProgress.upsert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<GalaxyProgress> observe(final int id) {
    final String _sql = "SELECT * FROM galaxy_progress WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"galaxy_progress"}, new Callable<GalaxyProgress>() {
      @Override
      @Nullable
      public GalaxyProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStage = CursorUtil.getColumnIndexOrThrow(_cursor, "stage");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfTotalSessionsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSessionsCompleted");
          final int _cursorIndexOfCurrentStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreakDays");
          final int _cursorIndexOfLongestStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "longestStreakDays");
          final int _cursorIndexOfLastSessionCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSessionCompletedAt");
          final int _cursorIndexOfMeteorStrikes = CursorUtil.getColumnIndexOrThrow(_cursor, "meteorStrikes");
          final GalaxyProgress _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final GalaxyStage _tmpStage;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStage);
            _tmpStage = __converters.stringToGalaxyStage(_tmp);
            final float _tmpProgress;
            _tmpProgress = _cursor.getFloat(_cursorIndexOfProgress);
            final int _tmpTotalSessionsCompleted;
            _tmpTotalSessionsCompleted = _cursor.getInt(_cursorIndexOfTotalSessionsCompleted);
            final int _tmpCurrentStreakDays;
            _tmpCurrentStreakDays = _cursor.getInt(_cursorIndexOfCurrentStreakDays);
            final int _tmpLongestStreakDays;
            _tmpLongestStreakDays = _cursor.getInt(_cursorIndexOfLongestStreakDays);
            final Long _tmpLastSessionCompletedAt;
            if (_cursor.isNull(_cursorIndexOfLastSessionCompletedAt)) {
              _tmpLastSessionCompletedAt = null;
            } else {
              _tmpLastSessionCompletedAt = _cursor.getLong(_cursorIndexOfLastSessionCompletedAt);
            }
            final int _tmpMeteorStrikes;
            _tmpMeteorStrikes = _cursor.getInt(_cursorIndexOfMeteorStrikes);
            _result = new GalaxyProgress(_tmpId,_tmpStage,_tmpProgress,_tmpTotalSessionsCompleted,_tmpCurrentStreakDays,_tmpLongestStreakDays,_tmpLastSessionCompletedAt,_tmpMeteorStrikes);
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
  public Object get(final int id, final Continuation<? super GalaxyProgress> $completion) {
    final String _sql = "SELECT * FROM galaxy_progress WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GalaxyProgress>() {
      @Override
      @Nullable
      public GalaxyProgress call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfStage = CursorUtil.getColumnIndexOrThrow(_cursor, "stage");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfTotalSessionsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSessionsCompleted");
          final int _cursorIndexOfCurrentStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreakDays");
          final int _cursorIndexOfLongestStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "longestStreakDays");
          final int _cursorIndexOfLastSessionCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSessionCompletedAt");
          final int _cursorIndexOfMeteorStrikes = CursorUtil.getColumnIndexOrThrow(_cursor, "meteorStrikes");
          final GalaxyProgress _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final GalaxyStage _tmpStage;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStage);
            _tmpStage = __converters.stringToGalaxyStage(_tmp);
            final float _tmpProgress;
            _tmpProgress = _cursor.getFloat(_cursorIndexOfProgress);
            final int _tmpTotalSessionsCompleted;
            _tmpTotalSessionsCompleted = _cursor.getInt(_cursorIndexOfTotalSessionsCompleted);
            final int _tmpCurrentStreakDays;
            _tmpCurrentStreakDays = _cursor.getInt(_cursorIndexOfCurrentStreakDays);
            final int _tmpLongestStreakDays;
            _tmpLongestStreakDays = _cursor.getInt(_cursorIndexOfLongestStreakDays);
            final Long _tmpLastSessionCompletedAt;
            if (_cursor.isNull(_cursorIndexOfLastSessionCompletedAt)) {
              _tmpLastSessionCompletedAt = null;
            } else {
              _tmpLastSessionCompletedAt = _cursor.getLong(_cursorIndexOfLastSessionCompletedAt);
            }
            final int _tmpMeteorStrikes;
            _tmpMeteorStrikes = _cursor.getInt(_cursorIndexOfMeteorStrikes);
            _result = new GalaxyProgress(_tmpId,_tmpStage,_tmpProgress,_tmpTotalSessionsCompleted,_tmpCurrentStreakDays,_tmpLongestStreakDays,_tmpLastSessionCompletedAt,_tmpMeteorStrikes);
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
