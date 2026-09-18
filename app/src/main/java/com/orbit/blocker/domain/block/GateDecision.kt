package com.orbit.blocker.domain.block

import com.orbit.blocker.data.model.BlockMode
import com.orbit.blocker.data.model.BlockRule
import com.orbit.blocker.domain.focus.FocusSessionState

/**
 * Pure decision logic for whether opening a package should trigger the quiz gate.
 * No Android or coroutine dependencies, so it is fully unit-testable.
 *
 * A package should be gated when BOTH:
 *  1. It is currently blocked (an active DURATION rule OR blocked by an active focus session), AND
 *  2. There is no unexpired access grant for it.
 */
object GateDecision {

    /**
     * @param packageName foreground package that just came into view
     * @param rules all block rules for [packageName] (any mode, enabled or not)
     * @param focus current focus session snapshot
     * @param hasActiveGrant whether an unexpired access grant exists for [packageName]
     * @param now current time in epoch millis
     */
    fun shouldGate(
        packageName: String,
        rules: List<BlockRule>,
        focus: FocusSessionState,
        hasActiveGrant: Boolean,
        now: Long = System.currentTimeMillis(),
    ): Boolean {
        if (hasActiveGrant) return false
        return isCurrentlyBlocked(packageName, rules, focus, now)
    }

    /** True if [packageName] is blocked right now by any rule or the active focus session. */
    fun isCurrentlyBlocked(
        packageName: String,
        rules: List<BlockRule>,
        focus: FocusSessionState,
        now: Long = System.currentTimeMillis(),
    ): Boolean {
        // An active focus session is authoritative: it blocks every package in its set,
        // regardless of whether a stored FOCUS_SESSION rule exists. This is what enables
        // "block all apps by default" (the session carries the full package set minus the
        // user's allow-list) without persisting a rule per installed app.
        if (focus.isPackageBlocked(packageName)) return true

        val relevant = rules.filter { it.packageName == packageName && it.enabled }

        val durationBlocked = relevant.any { rule ->
            rule.mode == BlockMode.DURATION && BlockRuleFactory.isDurationRuleActive(rule, now)
        }
        if (durationBlocked) return true

        val hasFocusRule = relevant.any { it.mode == BlockMode.FOCUS_SESSION }
        val focusBlocked = hasFocusRule && focus.isPackageBlocked(packageName)
        return focusBlocked
    }
}
