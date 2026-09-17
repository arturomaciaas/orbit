package com.orbit.blocker.data.db;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class OrbitDatabase_Impl extends OrbitDatabase {
  private volatile BlockedAppDao _blockedAppDao;

  private volatile BlockRuleDao _blockRuleDao;

  private volatile QuestionDao _questionDao;

  private volatile AccessGrantDao _accessGrantDao;

  private volatile NotificationRecordDao _notificationRecordDao;

  private volatile GalaxyProgressDao _galaxyProgressDao;

  private volatile FocusSessionDao _focusSessionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `blocked_apps` (`packageName` TEXT NOT NULL, `appLabel` TEXT NOT NULL, `notificationTier` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`packageName`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `block_rules` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `mode` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `expiresAt` INTEGER, `enabled` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_block_rules_packageName` ON `block_rules` (`packageName`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_block_rules_mode` ON `block_rules` (`mode`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `topic` TEXT NOT NULL, `prompt` TEXT NOT NULL, `choices` TEXT NOT NULL, `correctIndex` INTEGER NOT NULL, `explanation` TEXT, `seeded` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_questions_topic` ON `questions` (`topic`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `access_grants` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `grantedAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_access_grants_packageName` ON `access_grants` (`packageName`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_access_grants_expiresAt` ON `access_grants` (`expiresAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notification_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packageName` TEXT NOT NULL, `appLabel` TEXT NOT NULL, `title` TEXT, `text` TEXT, `tier` TEXT NOT NULL, `postedAt` INTEGER NOT NULL, `read` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notification_records_packageName` ON `notification_records` (`packageName`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notification_records_postedAt` ON `notification_records` (`postedAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `galaxy_progress` (`id` INTEGER NOT NULL, `stage` TEXT NOT NULL, `progress` REAL NOT NULL, `totalSessionsCompleted` INTEGER NOT NULL, `currentStreakDays` INTEGER NOT NULL, `longestStreakDays` INTEGER NOT NULL, `lastSessionCompletedAt` INTEGER, `meteorStrikes` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `focus_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER NOT NULL, `plannedDurationMillis` INTEGER NOT NULL, `outcome` TEXT NOT NULL, `blockedPackageCount` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_focus_sessions_startedAt` ON `focus_sessions` (`startedAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a4988cff3941a4d01224df3848a518dc')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `blocked_apps`");
        db.execSQL("DROP TABLE IF EXISTS `block_rules`");
        db.execSQL("DROP TABLE IF EXISTS `questions`");
        db.execSQL("DROP TABLE IF EXISTS `access_grants`");
        db.execSQL("DROP TABLE IF EXISTS `notification_records`");
        db.execSQL("DROP TABLE IF EXISTS `galaxy_progress`");
        db.execSQL("DROP TABLE IF EXISTS `focus_sessions`");
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
        final HashMap<String, TableInfo.Column> _columnsBlockedApps = new HashMap<String, TableInfo.Column>(4);
        _columnsBlockedApps.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockedApps.put("appLabel", new TableInfo.Column("appLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockedApps.put("notificationTier", new TableInfo.Column("notificationTier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockedApps.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBlockedApps = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBlockedApps = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBlockedApps = new TableInfo("blocked_apps", _columnsBlockedApps, _foreignKeysBlockedApps, _indicesBlockedApps);
        final TableInfo _existingBlockedApps = TableInfo.read(db, "blocked_apps");
        if (!_infoBlockedApps.equals(_existingBlockedApps)) {
          return new RoomOpenHelper.ValidationResult(false, "blocked_apps(com.orbit.blocker.data.model.BlockedApp).\n"
                  + " Expected:\n" + _infoBlockedApps + "\n"
                  + " Found:\n" + _existingBlockedApps);
        }
        final HashMap<String, TableInfo.Column> _columnsBlockRules = new HashMap<String, TableInfo.Column>(6);
        _columnsBlockRules.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockRules.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockRules.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockRules.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockRules.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlockRules.put("enabled", new TableInfo.Column("enabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBlockRules = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBlockRules = new HashSet<TableInfo.Index>(2);
        _indicesBlockRules.add(new TableInfo.Index("index_block_rules_packageName", false, Arrays.asList("packageName"), Arrays.asList("ASC")));
        _indicesBlockRules.add(new TableInfo.Index("index_block_rules_mode", false, Arrays.asList("mode"), Arrays.asList("ASC")));
        final TableInfo _infoBlockRules = new TableInfo("block_rules", _columnsBlockRules, _foreignKeysBlockRules, _indicesBlockRules);
        final TableInfo _existingBlockRules = TableInfo.read(db, "block_rules");
        if (!_infoBlockRules.equals(_existingBlockRules)) {
          return new RoomOpenHelper.ValidationResult(false, "block_rules(com.orbit.blocker.data.model.BlockRule).\n"
                  + " Expected:\n" + _infoBlockRules + "\n"
                  + " Found:\n" + _existingBlockRules);
        }
        final HashMap<String, TableInfo.Column> _columnsQuestions = new HashMap<String, TableInfo.Column>(7);
        _columnsQuestions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("topic", new TableInfo.Column("topic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("prompt", new TableInfo.Column("prompt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("choices", new TableInfo.Column("choices", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("correctIndex", new TableInfo.Column("correctIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("explanation", new TableInfo.Column("explanation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuestions.put("seeded", new TableInfo.Column("seeded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuestions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuestions = new HashSet<TableInfo.Index>(1);
        _indicesQuestions.add(new TableInfo.Index("index_questions_topic", false, Arrays.asList("topic"), Arrays.asList("ASC")));
        final TableInfo _infoQuestions = new TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions);
        final TableInfo _existingQuestions = TableInfo.read(db, "questions");
        if (!_infoQuestions.equals(_existingQuestions)) {
          return new RoomOpenHelper.ValidationResult(false, "questions(com.orbit.blocker.data.model.Question).\n"
                  + " Expected:\n" + _infoQuestions + "\n"
                  + " Found:\n" + _existingQuestions);
        }
        final HashMap<String, TableInfo.Column> _columnsAccessGrants = new HashMap<String, TableInfo.Column>(4);
        _columnsAccessGrants.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccessGrants.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccessGrants.put("grantedAt", new TableInfo.Column("grantedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccessGrants.put("expiresAt", new TableInfo.Column("expiresAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccessGrants = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAccessGrants = new HashSet<TableInfo.Index>(2);
        _indicesAccessGrants.add(new TableInfo.Index("index_access_grants_packageName", false, Arrays.asList("packageName"), Arrays.asList("ASC")));
        _indicesAccessGrants.add(new TableInfo.Index("index_access_grants_expiresAt", false, Arrays.asList("expiresAt"), Arrays.asList("ASC")));
        final TableInfo _infoAccessGrants = new TableInfo("access_grants", _columnsAccessGrants, _foreignKeysAccessGrants, _indicesAccessGrants);
        final TableInfo _existingAccessGrants = TableInfo.read(db, "access_grants");
        if (!_infoAccessGrants.equals(_existingAccessGrants)) {
          return new RoomOpenHelper.ValidationResult(false, "access_grants(com.orbit.blocker.data.model.AccessGrant).\n"
                  + " Expected:\n" + _infoAccessGrants + "\n"
                  + " Found:\n" + _existingAccessGrants);
        }
        final HashMap<String, TableInfo.Column> _columnsNotificationRecords = new HashMap<String, TableInfo.Column>(8);
        _columnsNotificationRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("packageName", new TableInfo.Column("packageName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("appLabel", new TableInfo.Column("appLabel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("text", new TableInfo.Column("text", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("tier", new TableInfo.Column("tier", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("postedAt", new TableInfo.Column("postedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationRecords.put("read", new TableInfo.Column("read", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotificationRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotificationRecords = new HashSet<TableInfo.Index>(2);
        _indicesNotificationRecords.add(new TableInfo.Index("index_notification_records_packageName", false, Arrays.asList("packageName"), Arrays.asList("ASC")));
        _indicesNotificationRecords.add(new TableInfo.Index("index_notification_records_postedAt", false, Arrays.asList("postedAt"), Arrays.asList("ASC")));
        final TableInfo _infoNotificationRecords = new TableInfo("notification_records", _columnsNotificationRecords, _foreignKeysNotificationRecords, _indicesNotificationRecords);
        final TableInfo _existingNotificationRecords = TableInfo.read(db, "notification_records");
        if (!_infoNotificationRecords.equals(_existingNotificationRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "notification_records(com.orbit.blocker.data.model.NotificationRecord).\n"
                  + " Expected:\n" + _infoNotificationRecords + "\n"
                  + " Found:\n" + _existingNotificationRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsGalaxyProgress = new HashMap<String, TableInfo.Column>(8);
        _columnsGalaxyProgress.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("stage", new TableInfo.Column("stage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("progress", new TableInfo.Column("progress", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("totalSessionsCompleted", new TableInfo.Column("totalSessionsCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("currentStreakDays", new TableInfo.Column("currentStreakDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("longestStreakDays", new TableInfo.Column("longestStreakDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("lastSessionCompletedAt", new TableInfo.Column("lastSessionCompletedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGalaxyProgress.put("meteorStrikes", new TableInfo.Column("meteorStrikes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGalaxyProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGalaxyProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGalaxyProgress = new TableInfo("galaxy_progress", _columnsGalaxyProgress, _foreignKeysGalaxyProgress, _indicesGalaxyProgress);
        final TableInfo _existingGalaxyProgress = TableInfo.read(db, "galaxy_progress");
        if (!_infoGalaxyProgress.equals(_existingGalaxyProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "galaxy_progress(com.orbit.blocker.data.model.GalaxyProgress).\n"
                  + " Expected:\n" + _infoGalaxyProgress + "\n"
                  + " Found:\n" + _existingGalaxyProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsFocusSessions = new HashMap<String, TableInfo.Column>(6);
        _columnsFocusSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("endedAt", new TableInfo.Column("endedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("plannedDurationMillis", new TableInfo.Column("plannedDurationMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("outcome", new TableInfo.Column("outcome", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFocusSessions.put("blockedPackageCount", new TableInfo.Column("blockedPackageCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFocusSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFocusSessions = new HashSet<TableInfo.Index>(1);
        _indicesFocusSessions.add(new TableInfo.Index("index_focus_sessions_startedAt", false, Arrays.asList("startedAt"), Arrays.asList("ASC")));
        final TableInfo _infoFocusSessions = new TableInfo("focus_sessions", _columnsFocusSessions, _foreignKeysFocusSessions, _indicesFocusSessions);
        final TableInfo _existingFocusSessions = TableInfo.read(db, "focus_sessions");
        if (!_infoFocusSessions.equals(_existingFocusSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "focus_sessions(com.orbit.blocker.data.model.FocusSessionRecord).\n"
                  + " Expected:\n" + _infoFocusSessions + "\n"
                  + " Found:\n" + _existingFocusSessions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "a4988cff3941a4d01224df3848a518dc", "e5010578d521e41fb9e8b53b9a09bdb0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "blocked_apps","block_rules","questions","access_grants","notification_records","galaxy_progress","focus_sessions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `blocked_apps`");
      _db.execSQL("DELETE FROM `block_rules`");
      _db.execSQL("DELETE FROM `questions`");
      _db.execSQL("DELETE FROM `access_grants`");
      _db.execSQL("DELETE FROM `notification_records`");
      _db.execSQL("DELETE FROM `galaxy_progress`");
      _db.execSQL("DELETE FROM `focus_sessions`");
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
    _typeConvertersMap.put(BlockRuleDao.class, BlockRuleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QuestionDao.class, QuestionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AccessGrantDao.class, AccessGrantDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationRecordDao.class, NotificationRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GalaxyProgressDao.class, GalaxyProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FocusSessionDao.class, FocusSessionDao_Impl.getRequiredConverters());
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
  public BlockRuleDao blockRuleDao() {
    if (_blockRuleDao != null) {
      return _blockRuleDao;
    } else {
      synchronized(this) {
        if(_blockRuleDao == null) {
          _blockRuleDao = new BlockRuleDao_Impl(this);
        }
        return _blockRuleDao;
      }
    }
  }

  @Override
  public QuestionDao questionDao() {
    if (_questionDao != null) {
      return _questionDao;
    } else {
      synchronized(this) {
        if(_questionDao == null) {
          _questionDao = new QuestionDao_Impl(this);
        }
        return _questionDao;
      }
    }
  }

  @Override
  public AccessGrantDao accessGrantDao() {
    if (_accessGrantDao != null) {
      return _accessGrantDao;
    } else {
      synchronized(this) {
        if(_accessGrantDao == null) {
          _accessGrantDao = new AccessGrantDao_Impl(this);
        }
        return _accessGrantDao;
      }
    }
  }

  @Override
  public NotificationRecordDao notificationRecordDao() {
    if (_notificationRecordDao != null) {
      return _notificationRecordDao;
    } else {
      synchronized(this) {
        if(_notificationRecordDao == null) {
          _notificationRecordDao = new NotificationRecordDao_Impl(this);
        }
        return _notificationRecordDao;
      }
    }
  }

  @Override
  public GalaxyProgressDao galaxyProgressDao() {
    if (_galaxyProgressDao != null) {
      return _galaxyProgressDao;
    } else {
      synchronized(this) {
        if(_galaxyProgressDao == null) {
          _galaxyProgressDao = new GalaxyProgressDao_Impl(this);
        }
        return _galaxyProgressDao;
      }
    }
  }

  @Override
  public FocusSessionDao focusSessionDao() {
    if (_focusSessionDao != null) {
      return _focusSessionDao;
    } else {
      synchronized(this) {
        if(_focusSessionDao == null) {
          _focusSessionDao = new FocusSessionDao_Impl(this);
        }
        return _focusSessionDao;
      }
    }
  }
}
