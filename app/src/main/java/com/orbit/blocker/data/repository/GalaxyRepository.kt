package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.GalaxyProgressDao
import com.orbit.blocker.data.model.GalaxyProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Persists gamification state (Task 7). Growth/setback math lives in the engine layer. */
interface GalaxyRepository {
    fun observe(): Flow<GalaxyProgress>
    suspend fun get(): GalaxyProgress
    suspend fun save(progress: GalaxyProgress)
    suspend fun ensureInitialized()
}

@Singleton
class GalaxyRepositoryImpl @Inject constructor(
    private val dao: GalaxyProgressDao,
) : GalaxyRepository {

    override fun observe(): Flow<GalaxyProgress> =
        dao.observe().map { it ?: GalaxyProgress() }

    override suspend fun get(): GalaxyProgress = dao.get() ?: GalaxyProgress()

    override suspend fun save(progress: GalaxyProgress) = dao.upsert(progress)

    override suspend fun ensureInitialized() = dao.insertIfAbsent(GalaxyProgress())
}
