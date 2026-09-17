package com.orbit.blocker.data.repository

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.db.DaoTestBase
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.domain.block.BlockRuleFactory
import com.orbit.blocker.domain.block.DurationUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Exercises the repository against a real in-memory Room DB, covering rule creation
 * and the expiry-cleanup path used by the WorkManager worker.
 */
class BlockRepositoryImplTest : DaoTestBase() {

    private val repo by lazy { BlockRepositoryImpl(db.blockedAppDao(), db.blockRuleDao()) }

    @Test
    fun addManagedApp_thenUpsertRule_isObservable() = runTest {
        repo.addManagedApp(BlockedApp(packageName = "com.x", appLabel = "X"))
        val now = 1_000_000L
        repo.upsertRule(BlockRuleFactory.durationRule("com.x", 2, DurationUnit.HOURS, now))

        val managed = repo.observeManagedApps().first()
        assertThat(managed.map { it.packageName }).containsExactly("com.x")

        val rules = repo.enabledRulesForPackage("com.x")
        assertThat(rules).hasSize(1)
        assertThat(rules.first().expiresAt).isEqualTo(now + TimeUnit.HOURS.toMillis(2))
    }

    @Test
    fun clearExpiredDurationRules_disablesOnlyExpired() = runTest {
        val now = 5_000_000L
        repo.addManagedApp(BlockedApp("a", "A"))
        repo.addManagedApp(BlockedApp("b", "B"))
        // Expired 1h block created 2h ago.
        repo.upsertRule(BlockRuleFactory.durationRule("a", 1, DurationUnit.HOURS, now - TimeUnit.HOURS.toMillis(2)))
        // Active 3h block created now.
        repo.upsertRule(BlockRuleFactory.durationRule("b", 3, DurationUnit.HOURS, now))

        val cleared = repo.clearExpiredDurationRules(now)
        assertThat(cleared).isEqualTo(1)
        assertThat(repo.enabledRulesForPackage("a")).isEmpty()
        assertThat(repo.enabledRulesForPackage("b")).hasSize(1)
    }

    @Test
    fun removeManagedApp_cascadesRuleDeletion() = runTest {
        repo.addManagedApp(BlockedApp("a", "A"))
        repo.upsertRule(BlockRuleFactory.focusSessionRule("a"))

        repo.removeManagedApp("a")

        assertThat(repo.observeManagedApps().first()).isEmpty()
        assertThat(repo.enabledRulesForPackage("a")).isEmpty()
    }
}
