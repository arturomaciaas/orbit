package com.orbit.blocker.data.repository

import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.NotificationTier
import kotlinx.coroutines.flow.Flow

/**
 * Manages the set of managed apps and their block rules. This is the data-layer
 * contract consumed by the app picker (Task 2) and the interception engine (Task 4).
 */
interface BlockRepository {
    fun observeManagedApps(): Flow<List<BlockedApp>>
    fun observeEnabledRules(): Flow<List<BlockRule>>
    fun observeAllRules(): Flow<List<BlockRule>>

    suspend fun addManagedApp(app: BlockedApp)
    suspend fun removeManagedApp(packageName: String)
    suspend fun setNotificationTier(packageName: String, tier: NotificationTier)

    suspend fun upsertRule(rule: BlockRule): Long
    suspend fun deleteRule(id: Long)
    suspend fun deleteRulesForPackage(packageName: String)
    suspend fun enabledRulesForPackage(packageName: String): List<BlockRule>

    /** Disables DURATION rules whose expiry has passed. Returns number cleared. */
    suspend fun clearExpiredDurationRules(now: Long): Int
}
