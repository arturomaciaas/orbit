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
 * Single-row table holding the current gamification state.
 *
 * The cosmos is now modeled as three nested layers instead of one morphing body:
 *
 *  - The **active planet** ([activePlanetType]) is what the user is growing right now.
 *    [stage] is its lifecycle stage and [progress] (0f..1f) is progress within that stage.
 *    Completing a focus session grows it; when it reaches its type's final stage it
 *    "locks in" to the current solar system and a fresh active planet begins.
 *  - The **solar system** is the set of [CompletedPlanet] rows for [currentSystemIndex],
 *    orbiting a central star. [planetsInSystem] mirrors that count for convenience.
 *  - The **galaxy** is the collection of completed solar systems: each finished system
 *    becomes one distant star. [systemsCompleted] counts them.
 *
 * A solar system is complete at [PLANETS_PER_SYSTEM] planets; completing it increments
 * [systemsCompleted], starts a new (empty) system, and begins a fresh active planet.
 */
@Entity(tableName = "galaxy_progress")
data class GalaxyProgress(
    @PrimaryKey val id: Int = SINGLETON_ID,
    /** Type of the planet currently being grown. */
    val activePlanetType: PlanetType = PlanetType.TERRAN,
    /** Lifecycle stage of the active planet. */
    val stage: PlanetStage = PlanetStage.PLANET,
    /** Progress (0f..1f) within the active planet's current [stage]. */
    val progress: Float = 0f,
    /** How many planets have been locked into the *current* (in-progress) solar system. */
    val planetsInSystem: Int = 0,
    /** Zero-based index of the solar system currently being built. */
    val currentSystemIndex: Int = 0,
    /** How many full solar systems have been completed (each becomes a star in the galaxy). */
    val systemsCompleted: Int = 0,
    val totalSessionsCompleted: Int = 0,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val lastSessionCompletedAt: Long? = null,
    val meteorStrikes: Int = 0,
    /**
     * Id of a [CompletedPlanet] that a meteor has marked for destruction but that hasn't
     * been animated/removed yet. Set at strike time; the Cosmos screen flies a meteor to
     * this planet and deletes it on impact, then clears this back to null. Survives process
     * death so the destruction is always shown, whichever screen fired the strike.
     */
    val doomedPlanetId: Long? = null,
) {
    companion object {
        const val SINGLETON_ID = 1

        /** Planets required to complete one solar system (8 planets + 1 star). */
        const val PLANETS_PER_SYSTEM = 8
    }
}

/**
 * A planet that has finished its lifecycle and locked into a solar system. Each row is
 * one orbiting body in the solar-system view. Rows are scoped to a [systemIndex] so a
 * completed system's planets are preserved even as the next system is built.
 *
 * A meteor strike destroys one of these rows (a random planet in the current system).
 */
@Entity(
    tableName = "completed_planets",
    indices = [Index("systemIndex")],
)
data class CompletedPlanet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Which solar system this planet belongs to (matches [GalaxyProgress.currentSystemIndex]). */
    val systemIndex: Int,
    /** Orbit slot (0-based) within the system, used to place it in the view. */
    val slot: Int,
    val type: PlanetType,
    val completedAt: Long = System.currentTimeMillis(),
)
