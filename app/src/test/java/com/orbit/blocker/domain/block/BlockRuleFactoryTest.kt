package com.orbit.blocker.domain.block

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.BlockMode
import org.junit.Test
import java.util.concurrent.TimeUnit

class BlockRuleFactoryTest {

    private val now = 1_000_000L

    @Test
    fun durationRule_computesExpiryFromAmountAndUnit() {
        val rule = BlockRuleFactory.durationRule("pkg", amount = 2, unit = DurationUnit.HOURS, now = now)

        assertThat(rule.mode).isEqualTo(BlockMode.DURATION)
        assertThat(rule.packageName).isEqualTo("pkg")
        assertThat(rule.expiresAt).isEqualTo(now + TimeUnit.HOURS.toMillis(2))
        assertThat(rule.enabled).isTrue()
    }

    @Test
    fun durationRule_supportsDaysAndMinutes() {
        val days = BlockRuleFactory.durationRule("p", 3, DurationUnit.DAYS, now)
        assertThat(days.expiresAt).isEqualTo(now + TimeUnit.DAYS.toMillis(3))

        val minutes = BlockRuleFactory.durationRule("p", 45, DurationUnit.MINUTES, now)
        assertThat(minutes.expiresAt).isEqualTo(now + TimeUnit.MINUTES.toMillis(45))
    }

    @Test(expected = IllegalArgumentException::class)
    fun durationRule_rejectsZeroAmount() {
        BlockRuleFactory.durationRule("p", amount = 0, unit = DurationUnit.HOURS, now = now)
    }

    @Test
    fun focusSessionRule_hasNoExpiry() {
        val rule = BlockRuleFactory.focusSessionRule("p", now)
        assertThat(rule.mode).isEqualTo(BlockMode.FOCUS_SESSION)
        assertThat(rule.expiresAt).isNull()
    }

    @Test
    fun isDurationRuleActive_trueBeforeExpiryFalseAfter() {
        val rule = BlockRuleFactory.durationRule("p", 1, DurationUnit.HOURS, now)
        assertThat(BlockRuleFactory.isDurationRuleActive(rule, now)).isTrue()
        assertThat(BlockRuleFactory.isDurationRuleActive(rule, now + TimeUnit.HOURS.toMillis(2))).isFalse()
    }

    @Test
    fun isDurationRuleActive_falseForFocusRules() {
        val focus = BlockRuleFactory.focusSessionRule("p", now)
        assertThat(BlockRuleFactory.isDurationRuleActive(focus, now)).isFalse()
    }

    @Test
    fun isDurationRuleActive_falseWhenDisabled() {
        val rule = BlockRuleFactory.durationRule("p", 1, DurationUnit.HOURS, now).copy(enabled = false)
        assertThat(BlockRuleFactory.isDurationRuleActive(rule, now)).isFalse()
    }

    @Test
    fun remainingMillis_flooredAtZeroAndNullForFocus() {
        val rule = BlockRuleFactory.durationRule("p", 1, DurationUnit.MINUTES, now)
        assertThat(BlockRuleFactory.remainingMillis(rule, now)).isEqualTo(TimeUnit.MINUTES.toMillis(1))
        assertThat(BlockRuleFactory.remainingMillis(rule, now + TimeUnit.MINUTES.toMillis(5))).isEqualTo(0L)

        val focus = BlockRuleFactory.focusSessionRule("p", now)
        assertThat(BlockRuleFactory.remainingMillis(focus, now)).isNull()
    }
}
