package com.orbit.blocker.domain.block

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.domain.focus.FocusSessionState
import org.junit.Test
import java.util.concurrent.TimeUnit

class GateDecisionTest {

    private val now = 1_000_000L
    private val pkg = "com.instagram.android"

    private fun durationRule(expiresAt: Long?, enabled: Boolean = true) = BlockRule(
        id = 1,
        packageName = pkg,
        mode = BlockMode.DURATION,
        createdAt = now,
        expiresAt = expiresAt,
        enabled = enabled,
    )

    private fun focusRule(enabled: Boolean = true) = BlockRule(
        id = 2,
        packageName = pkg,
        mode = BlockMode.FOCUS_SESSION,
        createdAt = now,
        expiresAt = null,
        enabled = enabled,
    )

    @Test
    fun gates_activeDurationBlock_noGrant() {
        val rules = listOf(durationRule(expiresAt = now + TimeUnit.HOURS.toMillis(1)))
        val gate = GateDecision.shouldGate(pkg, rules, FocusSessionState.INACTIVE, hasActiveGrant = false, now = now)
        assertThat(gate).isTrue()
    }

    @Test
    fun doesNotGate_whenActiveGrantExists() {
        val rules = listOf(durationRule(expiresAt = now + TimeUnit.HOURS.toMillis(1)))
        val gate = GateDecision.shouldGate(pkg, rules, FocusSessionState.INACTIVE, hasActiveGrant = true, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun doesNotGate_expiredDurationBlock() {
        val rules = listOf(durationRule(expiresAt = now - 1))
        val gate = GateDecision.shouldGate(pkg, rules, FocusSessionState.INACTIVE, hasActiveGrant = false, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun doesNotGate_disabledRule() {
        val rules = listOf(durationRule(expiresAt = now + TimeUnit.HOURS.toMillis(1), enabled = false))
        val gate = GateDecision.shouldGate(pkg, rules, FocusSessionState.INACTIVE, hasActiveGrant = false, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun gates_focusRule_whenSessionActiveAndPackageIncluded() {
        val rules = listOf(focusRule())
        val focus = FocusSessionState(active = true, blockedPackages = setOf(pkg), endsAt = now + 1000)
        val gate = GateDecision.shouldGate(pkg, rules, focus, hasActiveGrant = false, now = now)
        assertThat(gate).isTrue()
    }

    @Test
    fun doesNotGate_focusRule_whenSessionInactive() {
        val rules = listOf(focusRule())
        val gate = GateDecision.shouldGate(pkg, rules, FocusSessionState.INACTIVE, hasActiveGrant = false, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun doesNotGate_focusRule_whenPackageNotInSession() {
        val rules = listOf(focusRule())
        val focus = FocusSessionState(active = true, blockedPackages = setOf("com.other.app"), endsAt = now + 1000)
        val gate = GateDecision.shouldGate(pkg, rules, focus, hasActiveGrant = false, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun doesNotGate_whenNoRulesForPackage() {
        val gate = GateDecision.shouldGate(pkg, emptyList(), FocusSessionState.INACTIVE, hasActiveGrant = false, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun grantOverridesEvenActiveFocusBlock() {
        val rules = listOf(focusRule())
        val focus = FocusSessionState(active = true, blockedPackages = setOf(pkg), endsAt = now + 1000)
        val gate = GateDecision.shouldGate(pkg, rules, focus, hasActiveGrant = true, now = now)
        assertThat(gate).isFalse()
    }

    @Test
    fun ignoresRulesForOtherPackages() {
        val otherRule = durationRule(expiresAt = now + TimeUnit.HOURS.toMillis(1)).copy(packageName = "com.other")
        val gate = GateDecision.shouldGate(pkg, listOf(otherRule), FocusSessionState.INACTIVE, hasActiveGrant = false, now = now)
        assertThat(gate).isFalse()
    }
}
