package com.orbit.blocker.data.db

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.AccessGrant
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AccessGrantDaoTest : DaoTestBase() {

    private val dao get() = db.accessGrantDao()

    @Test
    fun activeGrantCount_reflectsExpiry() = runTest {
        val now = 100_000L
        dao.insert(AccessGrant(packageName = "com.app", grantedAt = now, expiresAt = now + 1000))

        // Before expiry -> active.
        assertThat(dao.activeGrantCount("com.app", now + 500)).isEqualTo(1)
        // After expiry -> inactive.
        assertThat(dao.activeGrantCount("com.app", now + 2000)).isEqualTo(0)
    }

    @Test
    fun deleteExpired_removesOnlyExpired() = runTest {
        val now = 100_000L
        dao.insert(AccessGrant(packageName = "a", grantedAt = now, expiresAt = now - 1))
        dao.insert(AccessGrant(packageName = "b", grantedAt = now, expiresAt = now + 5000))

        val removed = dao.deleteExpired(now)
        assertThat(removed).isEqualTo(1)
        assertThat(dao.activeGrantCount("b", now)).isEqualTo(1)
    }

    @Test
    fun latestActiveGrant_returnsFurthestExpiry() = runTest {
        val now = 0L
        dao.insert(AccessGrant(packageName = "a", grantedAt = now, expiresAt = 1000))
        dao.insert(AccessGrant(packageName = "a", grantedAt = now, expiresAt = 5000))

        val latest = dao.latestActiveGrant("a", now)
        assertThat(latest).isNotNull()
        assertThat(latest!!.expiresAt).isEqualTo(5000)
    }
}
