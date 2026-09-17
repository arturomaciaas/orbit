package com.orbit.blocker.domain.focus

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FocusTimerTest {

    @Test
    fun remainingMillis_flooredAtZero() {
        assertThat(FocusTimer.remainingMillis(endsAt = 1000, now = 400)).isEqualTo(600)
        assertThat(FocusTimer.remainingMillis(endsAt = 1000, now = 1500)).isEqualTo(0)
    }

    @Test
    fun isComplete_atOrAfterEnd() {
        assertThat(FocusTimer.isComplete(endsAt = 1000, now = 999)).isFalse()
        assertThat(FocusTimer.isComplete(endsAt = 1000, now = 1000)).isTrue()
        assertThat(FocusTimer.isComplete(endsAt = 1000, now = 1001)).isTrue()
    }

    @Test
    fun progressFraction_clampsAndHandlesZeroWindow() {
        assertThat(FocusTimer.progressFraction(startedAt = 0, endsAt = 100, now = 25)).isEqualTo(0.25f)
        assertThat(FocusTimer.progressFraction(startedAt = 0, endsAt = 100, now = -10)).isEqualTo(0f)
        assertThat(FocusTimer.progressFraction(startedAt = 0, endsAt = 100, now = 250)).isEqualTo(1f)
        assertThat(FocusTimer.progressFraction(startedAt = 50, endsAt = 50, now = 50)).isEqualTo(1f)
    }
}
