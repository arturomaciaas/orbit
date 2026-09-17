package com.orbit.blocker.domain.gamification

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.GalaxyStage
import com.orbit.blocker.data.repository.GalaxyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GamificationEventsTest {

    private class FakeGalaxyRepo : GalaxyRepository {
        var progress = GalaxyProgress()
        override fun observe(): Flow<GalaxyProgress> = flowOf(progress)
        override suspend fun get(): GalaxyProgress = progress
        override suspend fun save(progress: GalaxyProgress) { this.progress = progress }
        override suspend fun ensureInitialized() {}
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
    fun onWrongAnswer_persistsSetback() = runTest {
        val repo = FakeGalaxyRepo()
        repo.progress = GalaxyProgress(stage = GalaxyStage.RINGS, progress = 0.5f)
        val events = GamificationEvents(repo)

        events.onWrongAnswer(wrongCount = 2)

        assertThat(repo.progress.meteorStrikes).isEqualTo(2)
        assertThat(repo.progress.progress).isWithin(1e-4f).of(0.5f - GalaxyEngine.SETBACK_PER_WRONG * 2)
    }

    @Test
    fun onWrongAnswer_zeroIsNoOp() = runTest {
        val repo = FakeGalaxyRepo()
        val before = repo.progress
        val events = GamificationEvents(repo)

        events.onWrongAnswer(0)

        assertThat(repo.progress).isEqualTo(before)
    }
}
