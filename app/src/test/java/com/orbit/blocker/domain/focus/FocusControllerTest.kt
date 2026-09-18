package com.orbit.blocker.domain.focus

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.FocusOutcome
import com.orbit.blocker.data.model.FocusSessionRecord
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.repository.FocusSessionRepository
import com.orbit.blocker.data.repository.GalaxyRepository
import com.orbit.blocker.domain.gamification.GamificationEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import org.junit.Test

class FocusControllerTest {

    private class FakeFocusRepo : FocusSessionRepository {
        val recorded = mutableListOf<FocusSessionRecord>()
        override fun observeAll(): Flow<List<FocusSessionRecord>> = flowOf(recorded)
        override fun observeCompletedCount(): Flow<Int> = flowOf(recorded.count { it.outcome == FocusOutcome.COMPLETED })
        override suspend fun record(record: FocusSessionRecord): Long {
            recorded += record; return recorded.size.toLong()
        }
        override suspend fun clearAll() = recorded.clear()
    }

    private class FakeGalaxyRepo : GalaxyRepository {
        var progress = GalaxyProgress()
        val planets = mutableListOf<CompletedPlanet>()
        override fun observe(): Flow<GalaxyProgress> = flowOf(progress)
        override suspend fun get(): GalaxyProgress = progress
        override suspend fun save(progress: GalaxyProgress) { this.progress = progress }
        override suspend fun ensureInitialized() {}
        override fun observeSystemPlanets(systemIndex: Int): Flow<List<CompletedPlanet>> =
            flowOf(planets.filter { it.systemIndex == systemIndex })
        override suspend fun systemPlanets(systemIndex: Int): List<CompletedPlanet> =
            planets.filter { it.systemIndex == systemIndex }
        override suspend fun addCompletedPlanet(planet: CompletedPlanet): CompletedPlanet {
            val stored = planet.copy(id = planets.size.toLong() + 1)
            planets += stored
            return stored
        }
        override suspend fun removeCompletedPlanet(planet: CompletedPlanet) { planets.remove(planet) }
    }

    // Spy over the real GamificationEvents to count completion calls.
    private class SpyGamification : GamificationEvents(FakeGalaxyRepo()) {
        var completedCalls = 0
        override suspend fun onFocusSessionCompleted(now: Long, random: Random) { completedCalls++ }
    }

    private fun controller(repo: FakeFocusRepo, gam: SpyGamification, manager: FocusSessionManager) =
        FocusController(manager, repo, gam)

    @Test
    fun complete_recordsCompletedAndGrows() = runTest {
        val manager = FocusSessionManager()
        val repo = FakeFocusRepo()
        val gam = SpyGamification()
        val c = controller(repo, gam, manager)

        c.begin(setOf("a", "b"), startedAt = 0, endsAt = 1000)
        assertThat(manager.current().active).isTrue()

        c.complete(now = 1000)

        assertThat(manager.current().active).isFalse()
        assertThat(repo.recorded).hasSize(1)
        assertThat(repo.recorded.first().outcome).isEqualTo(FocusOutcome.COMPLETED)
        assertThat(repo.recorded.first().blockedPackageCount).isEqualTo(2)
        assertThat(gam.completedCalls).isEqualTo(1)
    }

    @Test
    fun abort_recordsAbortedNoGrowth() = runTest {
        val manager = FocusSessionManager()
        val repo = FakeFocusRepo()
        val gam = SpyGamification()
        val c = controller(repo, gam, manager)

        c.begin(setOf("a"), startedAt = 0, endsAt = 5000)
        c.abort(now = 1200)

        assertThat(manager.current().active).isFalse()
        assertThat(repo.recorded.single().outcome).isEqualTo(FocusOutcome.ABORTED)
        assertThat(gam.completedCalls).isEqualTo(0)
    }

    @Test
    fun completeWhenInactive_isNoOp() = runTest {
        val manager = FocusSessionManager()
        val repo = FakeFocusRepo()
        val gam = SpyGamification()
        val c = controller(repo, gam, manager)

        c.complete(now = 1000)

        assertThat(repo.recorded).isEmpty()
        assertThat(gam.completedCalls).isEqualTo(0)
    }
}
