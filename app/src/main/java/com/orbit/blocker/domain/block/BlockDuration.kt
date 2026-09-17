package com.orbit.blocker.domain.block

import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import java.util.concurrent.TimeUnit

/** Units the user can pick when creating a duration block. */
enum class DurationUnit(val label: String) {
    MINUTES("minutes"),
    HOURS("hours"),
    DAYS("days"),
    ;

    fun toMillis(amount: Long): Long = when (this) {
        MINUTES -> TimeUnit.MINUTES.toMillis(amount)
        HOURS -> TimeUnit.HOURS.toMillis(amount)
        DAYS -> TimeUnit.DAYS.toMillis(amount)
    }
}

/**
 * Pure functions for turning a user's block choice into a persisted [BlockRule].
 * Kept free of Android dependencies so it is unit-testable on the JVM.
 */
object BlockRuleFactory {

    /** Minimum sensible duration to avoid accidental zero/negative blocks. */
    const val MIN_AMOUNT = 1L

    /**
     * Builds a DURATION rule expiring [amount] [unit]s after [now].
     * @throws IllegalArgumentException if amount < [MIN_AMOUNT].
     */
    fun durationRule(
        packageName: String,
        amount: Long,
        unit: DurationUnit,
        now: Long = System.currentTimeMillis(),
    ): BlockRule {
        require(amount >= MIN_AMOUNT) { "Block amount must be at least $MIN_AMOUNT" }
        val expiresAt = now + unit.toMillis(amount)
        return BlockRule(
            packageName = packageName,
            mode = BlockMode.DURATION,
            createdAt = now,
            expiresAt = expiresAt,
            enabled = true,
        )
    }

    /** Builds a FOCUS_SESSION rule; enforcement is driven by an active session, not a clock. */
    fun focusSessionRule(
        packageName: String,
        now: Long = System.currentTimeMillis(),
    ): BlockRule = BlockRule(
        packageName = packageName,
        mode = BlockMode.FOCUS_SESSION,
        createdAt = now,
        expiresAt = null,
        enabled = true,
    )

    /**
     * True if [rule] is an active DURATION block at [now]. FOCUS_SESSION rules always
     * return false here because their activeness depends on session state, not expiry.
     */
    fun isDurationRuleActive(rule: BlockRule, now: Long = System.currentTimeMillis()): Boolean {
        if (rule.mode != BlockMode.DURATION || !rule.enabled) return false
        val expiry = rule.expiresAt ?: return false
        return now < expiry
    }

    /** Remaining milliseconds for a DURATION rule, floored at 0. Null for non-duration rules. */
    fun remainingMillis(rule: BlockRule, now: Long = System.currentTimeMillis()): Long? {
        if (rule.mode != BlockMode.DURATION) return null
        val expiry = rule.expiresAt ?: return null
        return (expiry - now).coerceAtLeast(0L)
    }
}
