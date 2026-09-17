package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.NotificationRecordDao
import com.orbit.blocker.data.model.NotificationRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Stores captured notifications for the digest (Task 6). */
interface NotificationRepository {
    fun observeAll(): Flow<List<NotificationRecord>>
    fun observeUnread(): Flow<List<NotificationRecord>>
    fun observeUnreadCount(): Flow<Int>
    suspend fun record(record: NotificationRecord): Long
    suspend fun markRead(id: Long)
    suspend fun markAllRead()
    suspend fun deleteOlderThan(before: Long): Int
    suspend fun clearAll()
}

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationRecordDao,
) : NotificationRepository {
    override fun observeAll(): Flow<List<NotificationRecord>> = dao.observeAll()
    override fun observeUnread(): Flow<List<NotificationRecord>> = dao.observeUnread()
    override fun observeUnreadCount(): Flow<Int> = dao.observeUnreadCount()
    override suspend fun record(record: NotificationRecord): Long = dao.insert(record)
    override suspend fun markRead(id: Long) = dao.markRead(id)
    override suspend fun markAllRead() = dao.markAllRead()
    override suspend fun deleteOlderThan(before: Long): Int = dao.deleteOlderThan(before)
    override suspend fun clearAll() = dao.deleteAll()
}
