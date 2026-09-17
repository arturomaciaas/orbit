package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.BlockRuleDao
import com.orbit.blocker.data.db.BlockedAppDao
import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.NotificationTier
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRepositoryImpl @Inject constructor(
    private val blockedAppDao: BlockedAppDao,
    private val blockRuleDao: BlockRuleDao,
) : BlockRepository {

    override fun observeManagedApps(): Flow<List<BlockedApp>> = blockedAppDao.observeAll()
    override fun observeEnabledRules(): Flow<List<BlockRule>> = blockRuleDao.observeEnabled()
    override fun observeAllRules(): Flow<List<BlockRule>> = blockRuleDao.observeAll()

    override suspend fun addManagedApp(app: BlockedApp) = blockedAppDao.upsert(app)
    override suspend fun removeManagedApp(packageName: String) {
        blockRuleDao.deleteByPackage(packageName)
        blockedAppDao.deleteByPackage(packageName)
    }

    override suspend fun setNotificationTier(packageName: String, tier: NotificationTier) =
        blockedAppDao.updateTier(packageName, tier)

    override suspend fun upsertRule(rule: BlockRule): Long = blockRuleDao.insert(rule)
    override suspend fun deleteRule(id: Long) = blockRuleDao.deleteById(id)
    override suspend fun deleteRulesForPackage(packageName: String) =
        blockRuleDao.deleteByPackage(packageName)

    override suspend fun enabledRulesForPackage(packageName: String): List<BlockRule> =
        blockRuleDao.getEnabledForPackage(packageName)

    override suspend fun clearExpiredDurationRules(now: Long): Int =
        blockRuleDao.disableExpiredDurationRules(now)
}
