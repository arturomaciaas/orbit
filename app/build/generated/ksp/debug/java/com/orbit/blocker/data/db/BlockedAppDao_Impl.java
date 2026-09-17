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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.orbit.blocker.data.model.BlockedApp;
import com.orbit.blocker.data.model.NotificationTier;
import java.lang.Class;
import java.lang.Exception;
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
public final class BlockedAppDao_Impl implements BlockedAppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BlockedApp> __insertionAdapterOfBlockedApp;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<BlockedApp> __deletionAdapterOfBlockedApp;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByPackage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTier;

  private final EntityUpsertionAdapter<BlockedApp> __upsertionAdapterOfBlockedApp;

  public BlockedAppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBlockedApp = new EntityInsertionAdapter<BlockedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `blocked_apps` (`packageName`,`appLabel`,`notificationTier`,`addedAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlockedApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppLabel());
        final String _tmp = __converters.notificationTierToString(entity.getNotificationTier());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getAddedAt());
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
        statement.bindString(1, entity.getPackageName());
      }
    };
    this.__preparedStmtOfDeleteByPackage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM blocked_apps WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM blocked_apps";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTier = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE blocked_apps SET notificationTier = ? WHERE packageName = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfBlockedApp = new EntityUpsertionAdapter<BlockedApp>(new EntityInsertionAdapter<BlockedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `blocked_apps` (`packageName`,`appLabel`,`notificationTier`,`addedAt`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlockedApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppLabel());
        final String _tmp = __converters.notificationTierToString(entity.getNotificationTier());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getAddedAt());
      }
    }, new EntityDeletionOrUpdateAdapter<BlockedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `blocked_apps` SET `packageName` = ?,`appLabel` = ?,`notificationTier` = ?,`addedAt` = ? WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlockedApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppLabel());
        final String _tmp = __converters.notificationTierToString(entity.getNotificationTier());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getAddedAt());
        statement.bindString(5, entity.getPackageName());
      }
    });
  }

  @Override
  public Object insertAll(final List<BlockedApp> apps,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBlockedApp.insert(apps);
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
  public Object deleteByPackage(final String packageName,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByPackage.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, packageName);
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
          __preparedStmtOfDeleteByPackage.release(_stmt);
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
  public Object updateTier(final String packageName, final NotificationTier tier,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTier.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.notificationTierToString(tier);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, packageName);
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
          __preparedStmtOfUpdateTier.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final BlockedApp app, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfBlockedApp.upsert(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BlockedApp>> observeAll() {
    final String _sql = "SELECT * FROM blocked_apps ORDER BY appLabel COLLATE NOCASE ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"blocked_apps"}, new Callable<List<BlockedApp>>() {
      @Override
      @NonNull
      public List<BlockedApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "appLabel");
          final int _cursorIndexOfNotificationTier = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationTier");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final List<BlockedApp> _result = new ArrayList<BlockedApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BlockedApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppLabel;
            _tmpAppLabel = _cursor.getString(_cursorIndexOfAppLabel);
            final NotificationTier _tmpNotificationTier;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNotificationTier);
            _tmpNotificationTier = __converters.stringToNotificationTier(_tmp);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new BlockedApp(_tmpPackageName,_tmpAppLabel,_tmpNotificationTier,_tmpAddedAt);
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
  public Object getByPackage(final String packageName,
      final Continuation<? super BlockedApp> $completion) {
    final String _sql = "SELECT * FROM blocked_apps WHERE packageName = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BlockedApp>() {
      @Override
      @Nullable
      public BlockedApp call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "appLabel");
          final int _cursorIndexOfNotificationTier = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationTier");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final BlockedApp _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppLabel;
            _tmpAppLabel = _cursor.getString(_cursorIndexOfAppLabel);
            final NotificationTier _tmpNotificationTier;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNotificationTier);
            _tmpNotificationTier = __converters.stringToNotificationTier(_tmp);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _result = new BlockedApp(_tmpPackageName,_tmpAppLabel,_tmpNotificationTier,_tmpAddedAt);
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
          final int _cursorIndexOfAppLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "appLabel");
          final int _cursorIndexOfNotificationTier = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationTier");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final List<BlockedApp> _result = new ArrayList<BlockedApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BlockedApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppLabel;
            _tmpAppLabel = _cursor.getString(_cursorIndexOfAppLabel);
            final NotificationTier _tmpNotificationTier;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfNotificationTier);
            _tmpNotificationTier = __converters.stringToNotificationTier(_tmp);
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _item = new BlockedApp(_tmpPackageName,_tmpAppLabel,_tmpNotificationTier,_tmpAddedAt);
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
