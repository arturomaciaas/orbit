package com.orbit.blocker.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.orbit.blocker.data.db.AccessGrantDao
import com.orbit.blocker.data.db.BlockRuleDao
import com.orbit.blocker.data.db.BlockedAppDao
import com.orbit.blocker.data.db.CompletedPlanetDao
import com.orbit.blocker.data.db.FocusSessionDao
import com.orbit.blocker.data.db.GalaxyProgressDao
import com.orbit.blocker.data.db.NotificationRecordDao
import com.orbit.blocker.data.db.OrbitDatabase
import com.orbit.blocker.data.db.QuestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * v2 -> v3: the gamification redesign. GalaxyProgress gained new columns and a new
     * completed_planets table was added. The gamification data is disposable, so this
     * migration simply drops the old galaxy table and (re)creates the new schema; Room then
     * repopulates the singleton row with defaults. Registered explicitly so the upgrade is
     * deterministic rather than relying on the destructive fallback.
     */
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS `galaxy_progress`")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `galaxy_progress` (" +
                    "`id` INTEGER NOT NULL, " +
                    "`activePlanetType` TEXT NOT NULL, " +
                    "`stage` TEXT NOT NULL, " +
                    "`progress` REAL NOT NULL, " +
                    "`planetsInSystem` INTEGER NOT NULL, " +
                    "`currentSystemIndex` INTEGER NOT NULL, " +
                    "`systemsCompleted` INTEGER NOT NULL, " +
                    "`totalSessionsCompleted` INTEGER NOT NULL, " +
                    "`currentStreakDays` INTEGER NOT NULL, " +
                    "`longestStreakDays` INTEGER NOT NULL, " +
                    "`lastSessionCompletedAt` INTEGER, " +
                    "`meteorStrikes` INTEGER NOT NULL, " +
                    "`doomedPlanetId` INTEGER, " +
                    "PRIMARY KEY(`id`))"
            )
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `completed_planets` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`systemIndex` INTEGER NOT NULL, " +
                    "`slot` INTEGER NOT NULL, " +
                    "`type` TEXT NOT NULL, " +
                    "`completedAt` INTEGER NOT NULL)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_completed_planets_systemIndex` " +
                    "ON `completed_planets` (`systemIndex`)"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OrbitDatabase =
        Room.databaseBuilder(context, OrbitDatabase::class.java, OrbitDatabase.NAME)
            .addMigrations(MIGRATION_2_3)
            // Safety net for any other version jump during early dev: recreate rather than crash.
            .fallbackToDestructiveMigration()
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides fun provideBlockedAppDao(db: OrbitDatabase): BlockedAppDao = db.blockedAppDao()
    @Provides fun provideBlockRuleDao(db: OrbitDatabase): BlockRuleDao = db.blockRuleDao()
    @Provides fun provideQuestionDao(db: OrbitDatabase): QuestionDao = db.questionDao()
    @Provides fun provideAccessGrantDao(db: OrbitDatabase): AccessGrantDao = db.accessGrantDao()
    @Provides fun provideNotificationDao(db: OrbitDatabase): NotificationRecordDao = db.notificationRecordDao()
    @Provides fun provideGalaxyDao(db: OrbitDatabase): GalaxyProgressDao = db.galaxyProgressDao()
    @Provides fun provideCompletedPlanetDao(db: OrbitDatabase): CompletedPlanetDao = db.completedPlanetDao()
    @Provides fun provideFocusSessionDao(db: OrbitDatabase): FocusSessionDao = db.focusSessionDao()
}
