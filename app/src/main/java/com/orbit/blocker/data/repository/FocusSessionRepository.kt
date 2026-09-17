package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.FocusSessionDao
import com.orbit.blocker.data.model.FocusSessionRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Persists focus-session history (Task 5); completed sessions feed gamification. */
interface FocusSessionRepository {
    fun observeAll(): Flow<List<FocusSessionRecord>>
    fun observeCompletedCount(): Flow<Int>
    suspend fun record(record: FocusSessionRecord): Long
    suspend fun clearAll()
}

@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    private val dao: FocusSessionDao,
) : FocusSessionRepository {
    override fun observeAll(): Flow<List<FocusSessionRecord>> = dao.observeAll()
    override fun observeCompletedCount(): Flow<Int> = dao.observeCompletedCount()
    override suspend fun record(record: FocusSessionRecord): Long = dao.insert(record)
    override suspend fun clearAll() = dao.deleteAll()
}
