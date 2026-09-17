package com.orbit.blocker.data.db

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.GalaxyStage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GalaxyProgressDaoTest : DaoTestBase() {

    private val dao get() = db.galaxyProgressDao()

    @Test
    fun insertIfAbsent_createsSingletonThenNoOps() = runTest {
        dao.insertIfAbsent(GalaxyProgress())
        dao.insertIfAbsent(GalaxyProgress(stage = GalaxyStage.GALAXY, progress = 0.9f))

        val row = dao.get()
        assertThat(row).isNotNull()
        // Second insert should have been ignored, leaving the default PLANET row.
        assertThat(row!!.stage).isEqualTo(GalaxyStage.PLANET)
    }

    @Test
    fun upsert_replacesSingletonRow() = runTest {
        dao.insertIfAbsent(GalaxyProgress())
        dao.upsert(GalaxyProgress(stage = GalaxyStage.RINGS, progress = 0.5f, totalSessionsCompleted = 7))

        val row = dao.get()!!
        assertThat(row.stage).isEqualTo(GalaxyStage.RINGS)
        assertThat(row.progress).isEqualTo(0.5f)
        assertThat(row.totalSessionsCompleted).isEqualTo(7)
    }

    @Test
    fun observe_emitsCurrentProgress() = runTest {
        dao.upsert(GalaxyProgress(stage = GalaxyStage.MOON, progress = 0.25f))
        val emitted = dao.observe().first()
        assertThat(emitted).isNotNull()
        assertThat(emitted!!.stage).isEqualTo(GalaxyStage.MOON)
    }
}
