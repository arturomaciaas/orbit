package com.orbit.blocker.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An app the user has chosen to manage, identified by its package name.
 * Display metadata is cached so lists render without re-querying PackageManager.
 */
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appLabel: String,
    val notificationTier: NotificationTier = NotificationTier.SUPPRESS,
    val addedAt: Long = System.currentTimeMillis(),
)

/**
 * A rule describing how/when an app is blocked. An app may have at most one active
 * rule per mode. For [BlockMode.DURATION], [expiresAt] is the wall-clock time the
 * block lifts; for [BlockMode.FOCUS_SESSION], [expiresAt] is null and enforcement is
 * driven by whether a focus session is currently active.
 */
@Entity(
    tableName = "block_rules",
    indices = [Index("packageName"), Index("mode")],
)
data class BlockRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val mode: BlockMode,
    val createdAt: Long = System.currentTimeMillis(),
    /** Wall-clock expiry for DURATION rules; null for FOCUS_SESSION rules. */
    val expiresAt: Long? = null,
    val enabled: Boolean = true,
)

/**
 * A single multiple-choice question in the local quiz bank.
 * [choices] is stored as a delimited string via a TypeConverter; [correctIndex]
 * points into that list.
 */
@Entity(
    tableName = "questions",
    indices = [Index("topic")],
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: QuizTopic,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String? = null,
    /** Seeded questions are marked so a "reset bank" can distinguish them from user-authored ones. */
    val seeded: Boolean = false,
)

/**
 * A time-boxed grant allowing access to a blocked app after passing the quiz gate.
 * Access is permitted while now < [expiresAt].
 */
@Entity(
    tableName = "access_grants",
    indices = [Index("packageName"), Index("expiresAt")],
)
data class AccessGrant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val grantedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long,
)

/**
 * A captured notification from a PEEK or SUPPRESS tier app, retained for the digest.
 */
@Entity(
    tableName = "notification_records",
    indices = [Index("packageName"), Index("postedAt")],
)
data class NotificationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appLabel: String,
    val title: String?,
    val text: String?,
    val tier: NotificationTier,
    val postedAt: Long = System.currentTimeMillis(),
    val read: Boolean = false,
)

/**
 * Single-row table holding the current gamification state. [progress] is a
 * continuous 0f..1f value within the current [stage]; crossing 1f advances the stage.
 */
@Entity(tableName = "galaxy_progress")
data class GalaxyProgress(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val stage: GalaxyStage = GalaxyStage.PLANET,
    val progress: Float = 0f,
    val totalSessionsCompleted: Int = 0,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val lastSessionCompletedAt: Long? = null,
    val meteorStrikes: Int = 0,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
