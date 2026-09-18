package com.orbit.blocker.data.backup

import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.FocusOutcome
import com.orbit.blocker.data.model.CompletedPlanet
import com.orbit.blocker.data.model.FocusSessionRecord
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.data.model.PlanetStage
import com.orbit.blocker.data.model.PlanetType
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic

/**
 * Pure entity <-> DTO mappers for backup. Enums are mapped by name; unknown names on
 * import fall back to safe defaults so a slightly newer/older file still restores.
 */
object BackupMappers {

    // region export (entity -> dto)
    fun BlockedApp.toDto() = BlockedAppDto(packageName, appLabel, notificationTier.name, addedAt)
    fun BlockRule.toDto() = BlockRuleDto(packageName, mode.name, createdAt, expiresAt, enabled)
    fun Question.toDto() = QuestionDto(topic.name, prompt, choices, correctIndex, explanation, seeded)
    fun GalaxyProgress.toDto() = GalaxyProgressDto(
        activePlanetType = activePlanetType.name,
        stage = stage.name,
        progress = progress,
        planetsInSystem = planetsInSystem,
        currentSystemIndex = currentSystemIndex,
        systemsCompleted = systemsCompleted,
        totalSessionsCompleted = totalSessionsCompleted,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        lastSessionCompletedAt = lastSessionCompletedAt,
        meteorStrikes = meteorStrikes,
        doomedPlanetId = doomedPlanetId,
    )
    fun CompletedPlanet.toDto() = CompletedPlanetDto(systemIndex, slot, type.name, completedAt)
    fun FocusSessionRecord.toDto() =
        FocusSessionDto(startedAt, endedAt, plannedDurationMillis, outcome.name, blockedPackageCount)
    // endregion

    // region import (dto -> entity)
    fun BlockedAppDto.toEntity() = BlockedApp(
        packageName = packageName,
        appLabel = appLabel,
        notificationTier = enumOrDefault(notificationTier, NotificationTier.SUPPRESS),
        addedAt = addedAt,
    )

    fun BlockRuleDto.toEntity() = BlockRule(
        packageName = packageName,
        mode = enumOrDefault(mode, BlockMode.DURATION),
        createdAt = createdAt,
        expiresAt = expiresAt,
        enabled = enabled,
    )

    fun QuestionDto.toEntity() = Question(
        topic = enumOrDefault(topic, QuizTopic.SOFTWARE_ENGINEERING),
        prompt = prompt,
        choices = choices,
        correctIndex = correctIndex.coerceIn(0, (choices.size - 1).coerceAtLeast(0)),
        explanation = explanation,
        seeded = seeded,
    )

    fun GalaxyProgressDto.toEntity() = GalaxyProgress(
        activePlanetType = enumOrDefault(activePlanetType, PlanetType.TERRAN),
        stage = enumOrDefault(stage, PlanetStage.PLANET),
        progress = progress.coerceIn(0f, 1f),
        planetsInSystem = planetsInSystem.coerceIn(0, GalaxyProgress.PLANETS_PER_SYSTEM),
        currentSystemIndex = currentSystemIndex.coerceAtLeast(0),
        systemsCompleted = systemsCompleted.coerceAtLeast(0),
        totalSessionsCompleted = totalSessionsCompleted,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        lastSessionCompletedAt = lastSessionCompletedAt,
        meteorStrikes = meteorStrikes,
        doomedPlanetId = doomedPlanetId,
    )

    fun CompletedPlanetDto.toEntity() = CompletedPlanet(
        systemIndex = systemIndex.coerceAtLeast(0),
        slot = slot.coerceAtLeast(0),
        type = enumOrDefault(type, PlanetType.TERRAN),
        completedAt = completedAt,
    )

    fun FocusSessionDto.toEntity() = FocusSessionRecord(
        startedAt = startedAt,
        endedAt = endedAt,
        plannedDurationMillis = plannedDurationMillis,
        outcome = enumOrDefault(outcome, FocusOutcome.COMPLETED),
        blockedPackageCount = blockedPackageCount,
    )
    // endregion

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String, default: T): T =
        enumValues<T>().firstOrNull { it.name == name } ?: default
}
