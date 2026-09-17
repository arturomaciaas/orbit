package com.orbit.blocker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import com.orbit.blocker.R
import com.orbit.blocker.data.model.NotificationRecord
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.data.repository.NotificationRepository
import com.orbit.blocker.domain.notification.IncomingNotification
import com.orbit.blocker.domain.notification.NotificationAction
import com.orbit.blocker.domain.notification.NotificationClassifier
import com.orbit.blocker.domain.notification.NotificationTierCache
import com.orbit.blocker.domain.notification.PeekContentBuilder
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Listens for posted notifications and applies the 3-tier policy:
 *  - PRIORITY / unmanaged / calls / ongoing: ignored (pass through with sound).
 *  - PEEK: cancel original, repost a trimmed silent notification, and store for digest.
 *  - SUPPRESS: cancel original and store for digest.
 *
 * Uses a Hilt EntryPoint (framework-instantiated service can't use constructor injection).
 */
class OrbitNotificationListenerService : NotificationListenerService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ListenerEntryPoint {
        fun tierCache(): NotificationTierCache
        fun notificationRepository(): NotificationRepository
    }

    private lateinit var tierCache: NotificationTierCache
    private lateinit var notificationRepository: NotificationRepository
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onListenerConnected() {
        super.onListenerConnected()
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ListenerEntryPoint::class.java,
        )
        tierCache = entryPoint.tierCache()
        notificationRepository = entryPoint.notificationRepository()
        tierCache.startObserving()
        ensurePeekChannel()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        // Never react to our own notifications (peek reposts / focus timer).
        if (sbn.packageName == packageName) return

        val incoming = sbn.toIncoming() ?: return
        val action = NotificationClassifier.classify(incoming) { pkg -> tierCache.tierFor(pkg) }
        if (action == NotificationAction.IGNORE) return

        val label = tierCache.labelFor(sbn.packageName) ?: sbn.packageName
        val tier = if (action == NotificationAction.PEEK) NotificationTier.PEEK else NotificationTier.SUPPRESS

        // Cancel the original so it doesn't show in the shade/lock screen.
        cancelNotification(sbn.key)

        scope.launch {
            notificationRepository.record(
                NotificationRecord(
                    packageName = sbn.packageName,
                    appLabel = label,
                    title = incoming.title,
                    text = incoming.text,
                    tier = tier,
                    postedAt = System.currentTimeMillis(),
                )
            )
            if (action == NotificationAction.PEEK) {
                repostPeek(label, incoming.title, incoming.text, sbn.id)
            }
        }
    }

    private fun repostPeek(appLabel: String, title: String?, text: String?, originalId: Int) {
        val content = PeekContentBuilder.build(title, text)
        val notification = NotificationCompat.Builder(this, PEEK_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$appLabel · ${content.sender}")
            .setContentText(content.preview)
            .setSilent(true)
            .setAutoCancel(true)
            .build()

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Offset the id so our repost doesn't collide with anything else.
        nm.notify(PEEK_ID_OFFSET + originalId, notification)
    }

    private fun StatusBarNotification.toIncoming(): IncomingNotification? {
        val extras = notification.extras ?: return null
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val isOngoing = (notification.flags and Notification.FLAG_ONGOING_EVENT) != 0
        val isGroupSummary = (notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0
        val category = notification.category
        val isCallStyle = category == Notification.CATEGORY_CALL

        return IncomingNotification(
            packageName = packageName,
            title = title,
            text = text,
            isOngoing = isOngoing,
            isGroupSummary = isGroupSummary,
            isCallStyle = isCallStyle,
        )
    }

    private fun ensurePeekChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(PEEK_CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    PEEK_CHANNEL_ID,
                    "Peek notifications",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply { description = "Silent trimmed previews from peek-tier apps" }
            )
        }
    }

    companion object {
        private const val PEEK_CHANNEL_ID = "orbit_peek"
        private const val PEEK_ID_OFFSET = 900_000
    }
}
