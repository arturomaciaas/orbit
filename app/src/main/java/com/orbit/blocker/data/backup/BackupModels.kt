package com.orbit.blocker.data.backup

import kotlinx.serialization.Serializable

/**
 * Versioned backup payload. Serialized to JSON for export via the Storage Access
 * Framework. DTOs are decoupled from Room entities so schema changes to either side
 * can be reconciled during import.
 */
@Serializable
data class OrbitBackup(
    val version: Int = CURRENT_VERSION,
    val exportedAt: Long,
    val managedApps: List<BlockedAppDto> = emptyList(),
    val blockRules: List<BlockRuleDto> = emptyList(),
    val questions: List<QuestionDto> = emptyList(),
    val galaxy: GalaxyProgressDto? = null,
    val completedPlanets: List<CompletedPlanetDto> = emptyList(),
    val focusSessions: List<FocusSessionDto> = emptyList(),
) {
    companion object {
        const val CURRENT_VERSION = 2
    }
}

@Serializable
data class BlockedAppDto(
    val packageName: String,
    val appLabel: String,
    val notificationTier: String,
    val addedAt: Long,
)

@Serializable
data class BlockRuleDto(
    val packageName: String,
    val mode: String,
    val createdAt: Long,
    val expiresAt: Long?,
    val enabled: Boolean,
)

@Serializable
data class QuestionDto(
    val topic: String,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String?,
    val seeded: Boolean,
)

@Serializable
data class GalaxyProgressDto(
    val activePlanetType: String = "TERRAN",
    val stage: String,
    val progress: Float,
    val planetsInSystem: Int = 0,
    val currentSystemIndex: Int = 0,
    val systemsCompleted: Int = 0,
    val totalSessionsCompleted: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val lastSessionCompletedAt: Long?,
    val meteorStrikes: Int,
    val doomedPlanetId: Long? = null,
)

@Serializable
data class CompletedPlanetDto(
    val systemIndex: Int,
    val slot: Int,
    val type: String,
    val completedAt: Long,
)

@Serializable
data class FocusSessionDto(
    val startedAt: Long,
    val endedAt: Long,
    val plannedDurationMillis: Long,
    val outcome: String,
    val blockedPackageCount: Int,
)
