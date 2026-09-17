package com.orbit.blocker.data.backup

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.backup.BackupMappers.toDto
import com.orbit.blocker.data.backup.BackupMappers.toEntity
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.data.model.BlockedApp
import com.orbit.blocker.data.model.FocusOutcome
import com.orbit.blocker.data.model.FocusSessionRecord
import com.orbit.blocker.data.model.GalaxyProgress
import com.orbit.blocker.data.model.GalaxyStage
import com.orbit.blocker.data.model.NotificationTier
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class BackupRoundTripTest {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private fun sampleBackup() = OrbitBackup(
        exportedAt = 1_700_000_000_000L,
        managedApps = listOf(
            BlockedApp("com.a", "App A", NotificationTier.PEEK, addedAt = 100).toDto(),
        ),
        blockRules = listOf(
            BlockRule(packageName = "com.a", mode = BlockMode.DURATION, createdAt = 1, expiresAt = 999, enabled = true).toDto(),
            BlockRule(packageName = "com.a", mode = BlockMode.FOCUS_SESSION, createdAt = 2, expiresAt = null).toDto(),
        ),
        questions = listOf(
            Question(topic = QuizTopic.AWS, prompt = "q", choices = listOf("a", "b"), correctIndex = 1, seeded = true).toDto(),
        ),
        galaxy = GalaxyProgress(stage = GalaxyStage.RINGS, progress = 0.5f, totalSessionsCompleted = 8, currentStreakDays = 3, longestStreakDays = 5, meteorStrikes = 2).toDto(),
        focusSessions = listOf(
            FocusSessionRecord(startedAt = 10, endedAt = 20, plannedDurationMillis = 10, outcome = FocusOutcome.COMPLETED, blockedPackageCount = 2).toDto(),
        ),
    )

    @Test
    fun jsonRoundTripPreservesPayload() {
        val original = sampleBackup()
        val text = json.encodeToString(original)
        val restored = json.decodeFromString<OrbitBackup>(text)
        assertThat(restored).isEqualTo(original)
    }

    @Test
    fun entityToDtoToEntityIsStable() {
        val app = BlockedApp("com.x", "X", NotificationTier.SUPPRESS, addedAt = 5)
        assertThat(app.toDto().toEntity()).isEqualTo(app)

        val rule = BlockRule(packageName = "com.x", mode = BlockMode.DURATION, createdAt = 1, expiresAt = 50, enabled = false)
        // id is not part of the DTO; compare the meaningful fields.
        val restoredRule = rule.toDto().toEntity()
        assertThat(restoredRule.packageName).isEqualTo(rule.packageName)
        assertThat(restoredRule.mode).isEqualTo(rule.mode)
        assertThat(restoredRule.expiresAt).isEqualTo(rule.expiresAt)
        assertThat(restoredRule.enabled).isEqualTo(rule.enabled)

        val galaxy = GalaxyProgress(stage = GalaxyStage.SYSTEM, progress = 0.7f, totalSessionsCompleted = 12)
        assertThat(galaxy.toDto().toEntity()).isEqualTo(galaxy)
    }

    @Test
    fun importToleratesUnknownEnumNames() {
        // Simulate a value from a newer version by hand-editing the DTO.
        val dto = BlockedAppDto("com.x", "X", notificationTier = "FUTURE_TIER", addedAt = 1)
        val entity = dto.toEntity()
        assertThat(entity.notificationTier).isEqualTo(NotificationTier.SUPPRESS) // safe fallback
    }

    @Test
    fun versionDefaultsToCurrent() {
        assertThat(sampleBackup().version).isEqualTo(OrbitBackup.CURRENT_VERSION)
    }
}
