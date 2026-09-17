package com.orbit.blocker.data.db

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BlockRuleDaoTest : DaoTestBase() {

    private val dao get() = db.blockRuleDao()

    @Test
    fun insertAndReadBackEnabledRule() = runTest {
        val id = dao.insert(
            BlockRule(packageName = "com.instagram.android", mode = BlockMode.DURATION, expiresAt = 999L)
        )
        assertThat(id).isGreaterThan(0L)

        val forPackage = dao.getEnabledForPackage("com.instagram.android")
        assertThat(forPackage).hasSize(1)
        assertThat(forPackage.first().mode).isEqualTo(BlockMode.DURATION)
    }

    @Test
    fun disableExpiredDurationRules_onlyClearsExpiredDurationRules() = runTest {
        val now = 10_000L
        // Expired duration rule -> should be disabled.
        dao.insert(BlockRule(packageName = "a", mode = BlockMode.DURATION, expiresAt = now - 1))
        // Future duration rule -> should stay enabled.
        dao.insert(BlockRule(packageName = "b", mode = BlockMode.DURATION, expiresAt = now + 1000))
        // Focus rule with null expiry -> should never be touched by duration cleanup.
        dao.insert(BlockRule(packageName = "c", mode = BlockMode.FOCUS_SESSION, expiresAt = null))

        val cleared = dao.disableExpiredDurationRules(now)
        assertThat(cleared).isEqualTo(1)

        assertThat(dao.getEnabledForPackage("a")).isEmpty()
        assertThat(dao.getEnabledForPackage("b")).hasSize(1)
        assertThat(dao.getEnabledForPackage("c")).hasSize(1)
    }

    @Test
    fun deleteByPackage_removesAllRulesForThatPackage() = runTest {
        dao.insert(BlockRule(packageName = "x", mode = BlockMode.DURATION, expiresAt = 1L))
        dao.insert(BlockRule(packageName = "x", mode = BlockMode.FOCUS_SESSION))
        dao.insert(BlockRule(packageName = "y", mode = BlockMode.DURATION, expiresAt = 1L))

        dao.deleteByPackage("x")

        assertThat(dao.getEnabledForPackage("x")).isEmpty()
        assertThat(dao.getEnabledForPackage("y")).hasSize(1)
    }
}
