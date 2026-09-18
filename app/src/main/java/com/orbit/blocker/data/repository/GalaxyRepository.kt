package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.CompletedPlanetDao
import com.orbit.blocker.data.db.GalaxyProgressDao
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists gamification state: the singleton [GalaxyProgress] row plus the
 * [CompletedPlanet] rows that make up each solar system. Growth/setback math lives in
 * the engine layer.
 */
interface GalaxyRepository {
    fun observe(): Flow<GalaxyProgress>
    suspend fun get(): GalaxyProgress
    suspend fun save(progress: GalaxyProgress)
    suspend fun ensureInitialized()

    /** Completed planets in the given solar system, ordered by orbit slot. */
    fun observeSystemPlanets(systemIndex: Int): Flow<List<CompletedPlanet>>
    suspend fun systemPlanets(systemIndex: Int): List<CompletedPlanet>
    suspend fun addCompletedPlanet(planet: CompletedPlanet): CompletedPlanet
    suspend fun removeCompletedPlanet(planet: CompletedPlanet)
}

@Singleton
class GalaxyRepositoryImpl @Inject constructor(
    private val dao: GalaxyProgressDao,
    private val planetDao: CompletedPlanetDao,
) : GalaxyRepository {

    override fun observe(): Flow<GalaxyProgress> =
        dao.observe().map { it ?: GalaxyProgress() }

    override suspend fun get(): GalaxyProgress = dao.get() ?: GalaxyProgress()

    override suspend fun save(progress: GalaxyProgress) = dao.upsert(progress)

    override suspend fun ensureInitialized() = dao.insertIfAbsent(GalaxyProgress())

    override fun observeSystemPlanets(systemIndex: Int): Flow<List<CompletedPlanet>> =
        planetDao.observeForSystem(systemIndex)

    override suspend fun systemPlanets(systemIndex: Int): List<CompletedPlanet> =
        planetDao.forSystem(systemIndex)

    override suspend fun addCompletedPlanet(planet: CompletedPlanet): CompletedPlanet {
        val id = planetDao.insert(planet)
        return planet.copy(id = id)
    }

    override suspend fun removeCompletedPlanet(planet: CompletedPlanet) =
        planetDao.delete(planet)
}
