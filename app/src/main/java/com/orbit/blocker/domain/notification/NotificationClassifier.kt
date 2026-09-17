package com.orbit.blocker.domain.notification

import com.orbit.blocker.data.model.NotificationTier

/**
 * Input describing an incoming notification, decoupled from Android's StatusBarNotification
 * so the classification + content logic is unit-testable on the JVM.
 */
data class IncomingNotification(
    val packageName: String,
    val title: String?,
    val text: String?,
    val isOngoing: Boolean,
    val isGroupSummary: Boolean,
    /** True if this notification is a call/ringing style (never suppressed). */
    val isCallStyle: Boolean = false,
)

/** What the listener should do with a notification. */
enum class NotificationAction {
    /** Leave it alone (priority apps, calls, unmanaged apps). */
    IGNORE,

    /** Cancel the original and repost a trimmed silent version; also store for digest. */
    PEEK,

    /** Cancel the original and store it for the digest; show nothing live. */
    SUPPRESS,
}

/**
 * Pure decision logic for notification handling.
 *
 * @param tierForPackage returns the configured [NotificationTier] for a package, or
 *   null if the package is not managed by Orbit (unmanaged => never touched).
 */
object NotificationClassifier {

    fun classify(
        notification: IncomingNotification,
        tierForPackage: (String) -> NotificationTier?,
    ): NotificationAction {
        // Never interfere with ongoing (foreground-service) or grouped-summary notifications,
        // and never touch call-style notifications regardless of tier.
        if (notification.isOngoing || notification.isGroupSummary || notification.isCallStyle) {
            return NotificationAction.IGNORE
        }

        return when (tierForPackage(notification.packageName)) {
            null, NotificationTier.PRIORITY -> NotificationAction.IGNORE
            NotificationTier.PEEK -> NotificationAction.PEEK
            NotificationTier.SUPPRESS -> NotificationAction.SUPPRESS
        }
    }
}
