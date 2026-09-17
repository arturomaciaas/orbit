package com.orbit.blocker.domain.notification

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.NotificationTier
import org.junit.Test

class NotificationClassifierTest {

    private fun incoming(
        pkg: String = "com.whatsapp",
        ongoing: Boolean = false,
        groupSummary: Boolean = false,
        callStyle: Boolean = false,
    ) = IncomingNotification(
        packageName = pkg,
        title = "Alice",
        text = "hey there",
        isOngoing = ongoing,
        isGroupSummary = groupSummary,
        isCallStyle = callStyle,
    )

    @Test
    fun unmanagedPackageIsIgnored() {
        val action = NotificationClassifier.classify(incoming("com.random")) { null }
        assertThat(action).isEqualTo(NotificationAction.IGNORE)
    }

    @Test
    fun priorityTierIsIgnored() {
        val action = NotificationClassifier.classify(incoming()) { NotificationTier.PRIORITY }
        assertThat(action).isEqualTo(NotificationAction.IGNORE)
    }

    @Test
    fun peekTierMapsToPeek() {
        val action = NotificationClassifier.classify(incoming()) { NotificationTier.PEEK }
        assertThat(action).isEqualTo(NotificationAction.PEEK)
    }

    @Test
    fun suppressTierMapsToSuppress() {
        val action = NotificationClassifier.classify(incoming()) { NotificationTier.SUPPRESS }
        assertThat(action).isEqualTo(NotificationAction.SUPPRESS)
    }

    @Test
    fun callStyleAlwaysIgnoredEvenIfSuppress() {
        val action = NotificationClassifier.classify(incoming(callStyle = true)) { NotificationTier.SUPPRESS }
        assertThat(action).isEqualTo(NotificationAction.IGNORE)
    }

    @Test
    fun ongoingAndGroupSummaryIgnored() {
        assertThat(NotificationClassifier.classify(incoming(ongoing = true)) { NotificationTier.SUPPRESS })
            .isEqualTo(NotificationAction.IGNORE)
        assertThat(NotificationClassifier.classify(incoming(groupSummary = true)) { NotificationTier.PEEK })
            .isEqualTo(NotificationAction.IGNORE)
    }
}
