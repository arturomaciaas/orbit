package com.orbit.blocker.domain.block

/**
 * Decides whether the quiz gate should be suppressed because the user is on a call.
 *
 * Orbit must never throw a full-screen gate over an active call (it could hang up a real
 * phone call, or disrupt a VoIP call). Historically this was decided purely from
 * `AudioManager.mode == MODE_IN_COMMUNICATION`. That signal is unreliable: VoIP apps
 * (WhatsApp, Meet, etc.) frequently leave the audio mode stuck in communication state long
 * after a call ends and never reset it. When that happens the gate was suppressed for every
 * app indefinitely — effectively disabling all blocking until reboot.
 *
 * This pure helper hardens that logic. The audio communication mode (which covers both
 * cellular `MODE_IN_CALL` and VoIP `MODE_IN_COMMUNICATION`) is honored ONLY within
 * [maxCommunicationSuppressionMillis] of when it was first observed. That cap is longer than
 * any realistic call but bounded, so a mode that gets stuck can no longer disable blocking
 * forever: once the window lapses the stale mode is ignored and gating resumes. When the mode
 * genuinely clears (normal call end) the tracker resets immediately, so back-to-back real
 * calls each get the full window.
 *
 * No Android dependencies, so the decision is unit-testable.
 */
class CallStateGate(
    /**
     * How long a standalone "audio mode is in communication" signal is trusted as a real call,
     * measured from when the mode first turned on. Beyond this the (likely stuck) mode is
     * ignored. Four hours easily covers any real call while guaranteeing self-recovery.
     */
    private val maxCommunicationSuppressionMillis: Long = DEFAULT_MAX_COMMUNICATION_SUPPRESSION_MILLIS,
) {
    /** Wall-clock time the audio mode was first seen in communication state; null when idle. */
    private var communicationSince: Long? = null

    /**
     * @param audioInCommunication true when [android.media.AudioManager.getMode] is
     *   `MODE_IN_CALL` or `MODE_IN_COMMUNICATION`.
     * @return true if the gate should be suppressed right now.
     */
    fun shouldSuppressGate(
        audioInCommunication: Boolean,
        now: Long,
    ): Boolean {
        // Track when the communication mode window began / clear it when the mode drops. A clean
        // call end resets this, so a subsequent call gets a fresh full window.
        if (audioInCommunication) {
            if (communicationSince == null) communicationSince = now
        } else {
            communicationSince = null
        }

        // Honor the communication mode only while it's fresh enough to plausibly be a live call.
        // A stuck mode ages out and stops suppressing, so blocking always recovers.
        val since = communicationSince ?: return false
        return (now - since) < maxCommunicationSuppressionMillis
    }

    companion object {
        const val DEFAULT_MAX_COMMUNICATION_SUPPRESSION_MILLIS = 4 * 60 * 60 * 1000L // 4 hours
    }
}
