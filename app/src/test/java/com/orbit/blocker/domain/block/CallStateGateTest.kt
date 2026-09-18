package com.orbit.blocker.domain.block

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CallStateGateTest {

    private val cap = 4 * 60 * 60 * 1000L // matches the default 4-hour window

    @Test
    fun noCall_doesNotSuppress() {
        val gate = CallStateGate(cap)
        assertThat(gate.shouldSuppressGate(audioInCommunication = false, now = 0)).isFalse()
    }

    @Test
    fun call_suppressesWhileFresh() {
        val gate = CallStateGate(cap)
        // Mode turns on at t=0, still within the window a few minutes later.
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = 0)).isTrue()
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = 5 * 60 * 1000L)).isTrue()
    }

    @Test
    fun stuckCommunicationMode_stopsSuppressingAfterCap() {
        val gate = CallStateGate(cap)
        gate.shouldSuppressGate(audioInCommunication = true, now = 0) // window starts at 0
        // Just before the cap: still suppressed.
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = cap - 1)).isTrue()
        // At/after the cap: the stuck mode is ignored and gating resumes.
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = cap)).isFalse()
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = cap + 60_000)).isFalse()
    }

    @Test
    fun cleanCallEnd_resetsWindowForNextCall() {
        val gate = CallStateGate(cap)
        gate.shouldSuppressGate(audioInCommunication = true, now = 0)
        // Mode drops (call ended cleanly) -> tracker resets.
        assertThat(gate.shouldSuppressGate(audioInCommunication = false, now = 10_000)).isFalse()
        // A new call much later gets a fresh full window rather than inheriting the old start.
        val laterStart = cap * 3
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = laterStart)).isTrue()
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = laterStart + cap - 1)).isTrue()
    }

    @Test
    fun windowMeasuredFromFirstObservation_notEachCall() {
        val gate = CallStateGate(cap)
        // Continuous communication mode from t=0; the window is anchored at 0 and expires at cap,
        // even though we keep observing the mode as "on".
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = 0)).isTrue()
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = cap / 2)).isTrue()
        assertThat(gate.shouldSuppressGate(audioInCommunication = true, now = cap)).isFalse()
    }
}
