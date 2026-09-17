package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.AccessGrantDao
import com.orbit.blocker.data.model.AccessGrant
import javax.inject.Inject
import javax.inject.Singleton

/** Tracks time-boxed access grants issued after passing the quiz gate (Task 4). */
interface AccessGrantRepository {
    suspend fun grantAccess(packageName: String, durationMillis: Long): AccessGrant
    suspend fun hasActiveGrant(packageName: String, now: Long = System.currentTimeMillis()): Boolean
    suspend fun latestActiveGrant(packageName: String, now: Long = System.currentTimeMillis()): AccessGrant?
    suspend fun purgeExpired(now: Long = System.currentTimeMillis()): Int
    suspend fun clearAll()
}

@Singleton
class AccessGrantRepositoryImpl @Inject constructor(
    private val dao: AccessGrantDao,
) : AccessGrantRepository {

    override suspend fun grantAccess(packageName: String, durationMillis: Long): AccessGrant {
        val now = System.currentTimeMillis()
        val grant = AccessGrant(
            packageName = packageName,
            grantedAt = now,
            expiresAt = now + durationMillis,
        )
        val id = dao.insert(grant)
        return grant.copy(id = id)
    }

    override suspend fun hasActiveGrant(packageName: String, now: Long): Boolean =
        dao.activeGrantCount(packageName, now) > 0

    override suspend fun latestActiveGrant(packageName: String, now: Long): AccessGrant? =
        dao.latestActiveGrant(packageName, now)

    override suspend fun purgeExpired(now: Long): Int = dao.deleteExpired(now)

    override suspend fun clearAll() = dao.deleteAll()
}
