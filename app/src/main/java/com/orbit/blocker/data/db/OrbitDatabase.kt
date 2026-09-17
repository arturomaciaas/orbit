package com.orbit.blocker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.orbit.blocker.data.model.AccessGrant
import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.FocusSessionRecord
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.NotificationRecord
import com.orbit.blocker.data.model.Question

@Database(
    entities = [
        BlockedApp::class,
        BlockRule::class,
        Question::class,
        AccessGrant::class,
        NotificationRecord::class,
        GalaxyProgress::class,
        FocusSessionRecord::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class OrbitDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun blockRuleDao(): BlockRuleDao
    abstract fun questionDao(): QuestionDao
    abstract fun accessGrantDao(): AccessGrantDao
    abstract fun notificationRecordDao(): NotificationRecordDao
    abstract fun galaxyProgressDao(): GalaxyProgressDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        const val NAME = "orbit.db"
    }
}
