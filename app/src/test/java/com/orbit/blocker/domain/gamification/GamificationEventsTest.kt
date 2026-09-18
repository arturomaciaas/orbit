package com.orbit.blocker.domain.gamification

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import com.orbit.blocker.data.repository.GalaxyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.random.Random

class GamificationEventsTest {

    private class FakeGalaxyRepo : GalaxyRepository {
        var progress = GalaxyProgress()
        val planets = mutableListOf<CompletedPlanet>()
        private var nextId = 1L

        override fun observe(): Flow<GalaxyProgress> = flowOf(progress)
        override suspend fun get(): GalaxyProgress = progress
        override suspend fun save(progress: GalaxyProgress) { this.progress = progress }
        override suspend fun ensureInitialized() {}

        override fun observeSystemPlanets(systemIndex: Int): Flow<List<CompletedPlanet>> =
            flowOf(planets.filter { it.systemIndex == systemIndex })
        override suspend fun systemPlanets(systemIndex: Int): List<CompletedPlanet> =
            planets.filter { it.systemIndex == systemIndex }
        override suspend fun addCompletedPlanet(planet: CompletedPlanet): CompletedPlanet {
            val saved = planet.copy(id = nextId++)
            planets += saved
            return saved
        }
        override suspend fun removeCompletedPlanet(planet: CompletedPlanet) {
            planets.removeAll { it.id == planet.id }
        }
    }

    @Test
    fun onFocusSessionCompleted_persistsGrowth() = runTest {
        val repo = FakeGalaxyRepo()
        val events = GamificationEvents(repo)

        events.onFocusSessionCompleted(now = 86_400_000L)

        assertThat(repo.progress.totalSessionsCompleted).isEqualTo(1)
        assertThat(repo.progress.progress).isWithin(1e-4f).of(GalaxyEngine.GROWTH_PER_SESSION)
    }

    @Test
    fun onFocusSessionCompleted_locksInCompletedPlanet() = runTest {
        val repo = FakeGalaxyRepo()
        // Terran one session from completing at MOON.
        repo.progress = GalaxyProgress(
            activePlanetType = PlanetType.TERRAN,
            stage = PlanetStage.MOON,
            progress = 0.9f,
        )
        val events = GamificationEvents(repo)

        events.onFocusSessionCompleted(now = 86_400_000L, random = Random(1))

        assertThat(repo.planets).hasSize(1)
        assertThat(repo.planets.first().type).isEqualTo(PlanetType.TERRAN)
        assertThat(repo.progress.planetsInSystem).isEqualTo(1)
    }

    @Test
    fun onMeteorStrike_marksAPlanetButDoesNotDeleteUntilImpact() = runTest {
        val repo = FakeGalaxyRepo()
        repo.progress = GalaxyProgress(planetsInSystem = 2, currentSystemIndex = 0)
        repo.planets += CompletedPlanet(id = 1, systemIndex = 0, slot = 0, type = PlanetType.TERRAN)
        repo.planets += CompletedPlanet(id = 2, systemIndex = 0, slot = 1, type = PlanetType.DWARF)
        val events = GamificationEvents(repo)

        events.onMeteorStrike(random = Random(3))

        // Marked, not yet removed.
        assertThat(repo.planets).hasSize(2)
        assertThat(repo.progress.planetsInSystem).isEqualTo(2)
        assertThat(repo.progress.meteorStrikes).isEqualTo(1)
        assertThat(repo.progress.doomedPlanetId).isAnyOf(1L, 2L)
    }

    @Test
    fun resolveMeteorImpact_deletesDoomedPlanetAndClearsMarker() = runTest {
        val repo = FakeGalaxyRepo()
        repo.progress = GalaxyProgress(planetsInSystem = 2, currentSystemIndex = 0, doomedPlanetId = 2L)
        repo.planets += CompletedPlanet(id = 1, systemIndex = 0, slot = 0, type = PlanetType.TERRAN)
        repo.planets += CompletedPlanet(id = 2, systemIndex = 0, slot = 1, type = PlanetType.DWARF)
        val events = GamificationEvents(repo)

        events.resolveMeteorImpact()

        assertThat(repo.planets.map { it.id }).containsExactly(1L)
        assertThat(repo.progress.planetsInSystem).isEqualTo(1)
        assertThat(repo.progress.doomedPlanetId).isNull()
    }

    @Test
    fun onMeteorStrike_setsBackActivePlanetWhenSystemEmpty() = runTest {
        val repo = FakeGalaxyRepo()
        repo.progress = GalaxyProgress(
            activePlanetType = PlanetType.TERRAN,
            stage = PlanetStage.MOON,
            progress = 0.5f,
        )
        val events = GamificationEvents(repo)

        events.onMeteorStrike(random = Random(0))

        assertThat(repo.planets).isEmpty()
        assertThat(repo.progress.meteorStrikes).isEqualTo(1)
        assertThat(repo.progress.progress).isLessThan(0.5f)
    }
}
