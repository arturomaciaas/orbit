package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.NotificationTier
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Upsert
    suspend fun upsert(app: BlockedApp)

    @Delete
    suspend fun delete(app: BlockedApp)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("SELECT * FROM blocked_apps ORDER BY appLabel COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<BlockedApp>>

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackage(packageName: String): BlockedApp?

    @Query("SELECT * FROM blocked_apps")
    suspend fun getAll(): List<BlockedApp>

    @Query("DELETE FROM blocked_apps")
    suspend fun deleteAll()

    @Query("UPDATE blocked_apps SET notificationTier = :tier WHERE packageName = :packageName")
    suspend fun updateTier(packageName: String, tier: NotificationTier)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<BlockedApp>)
}
