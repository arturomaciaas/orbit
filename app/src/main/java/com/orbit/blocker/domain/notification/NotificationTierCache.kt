package com.orbit.blocker.domain.notification

import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.data.repository.BlockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory cache of package -> configured [NotificationTier] and label, kept fresh
 * from the DB so the NotificationListenerService can classify synchronously without
 * a DB round-trip on every posted notification.
 */
@Singleton
class NotificationTierCache @Inject constructor(
    private val blockRepository: BlockRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val tiers = ConcurrentHashMap<String, NotificationTier>()
    private val labels = ConcurrentHashMap<String, String>()

    fun startObserving() {
        blockRepository.observeManagedApps()
            .onEach { apps ->
                tiers.clear()
                labels.clear()
                apps.forEach {
                    tiers[it.packageName] = it.notificationTier
                    labels[it.packageName] = it.appLabel
                }
            }
            .launchIn(scope)
    }

    /** Tier for [packageName], or null if the app is unmanaged (do not touch). */
    fun tierFor(packageName: String): NotificationTier? = tiers[packageName]

    fun labelFor(packageName: String): String? = labels[packageName]
}
