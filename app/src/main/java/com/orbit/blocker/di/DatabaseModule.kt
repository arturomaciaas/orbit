package com.orbit.blocker.di

import android.content.Context
import androidx.room.Room
import com.orbit.blocker.data.db.AccessGrantDao
import com.orbit.blocker.data.db.BlockRuleDao
import com.orbit.blocker.data.db.BlockedAppDao
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OrbitDatabase =
        Room.databaseBuilder(context, OrbitDatabase::class.java, OrbitDatabase.NAME)
            // Personal app, single version so far; recreate on schema change during early dev.
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideBlockedAppDao(db: OrbitDatabase): BlockedAppDao = db.blockedAppDao()
    @Provides fun provideBlockRuleDao(db: OrbitDatabase): BlockRuleDao = db.blockRuleDao()
    @Provides fun provideQuestionDao(db: OrbitDatabase): QuestionDao = db.questionDao()
    @Provides fun provideAccessGrantDao(db: OrbitDatabase): AccessGrantDao = db.accessGrantDao()
    @Provides fun provideNotificationDao(db: OrbitDatabase): NotificationRecordDao = db.notificationRecordDao()
    @Provides fun provideGalaxyDao(db: OrbitDatabase): GalaxyProgressDao = db.galaxyProgressDao()
    @Provides fun provideFocusSessionDao(db: OrbitDatabase): FocusSessionDao = db.focusSessionDao()
}
