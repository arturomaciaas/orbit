package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orbit.blocker.data.model.NotificationRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationRecordDao {

    @Insert
    suspend fun insert(record: NotificationRecord): Long

    @Insert
    suspend fun insertAll(records: List<NotificationRecord>)

    @Query("SELECT * FROM notification_records ORDER BY postedAt DESC")
    fun observeAll(): Flow<List<NotificationRecord>>

    @Query("SELECT * FROM notification_records WHERE read = 0 ORDER BY postedAt DESC")
    fun observeUnread(): Flow<List<NotificationRecord>>

    @Query("SELECT COUNT(*) FROM notification_records WHERE read = 0")
    fun observeUnreadCount(): Flow<Int>

    @Query("UPDATE notification_records SET read = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE notification_records SET read = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM notification_records WHERE postedAt < :before")
    suspend fun deleteOlderThan(before: Long): Int

    @Query("DELETE FROM notification_records")
    suspend fun deleteAll()

    @Query("SELECT * FROM notification_records")
    suspend fun getAll(): List<NotificationRecord>
}
