package com.orbit.blocker.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.view.accessibility.AccessibilityEvent
import com.orbit.blocker.domain.block.BlockEnforcer
import com.orbit.blocker.gate.QuizGateActivity
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Detects foreground app changes and launches the quiz gate over blocked apps.
 *
 * Uses a Hilt [EntryPoint] to obtain dependencies because framework-instantiated
 * services can't use constructor injection. Debounces repeated events for the same
 * package and skips our own gate/UI to avoid loops.
 */
class OrbitAccessibilityService : AccessibilityService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ServiceEntryPoint {
        fun blockEnforcer(): BlockEnforcer
    }

    private lateinit var blockEnforcer: BlockEnforcer
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Debounce: ignore the same package within this window, and while a gate is pending.
    private var lastPackage: String? = null
    private var lastEventAt: Long = 0L
    @Volatile private var gatePending: Boolean = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ServiceEntryPoint::class.java,
        )
        blockEnforcer = entryPoint.blockEnforcer()
        blockEnforcer.startObserving()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        // Skip our own app so the gate/back-to-home flow doesn't loop.
        if (pkg == packageName) return

        val now = System.currentTimeMillis()
        if (pkg == lastPackage && now - lastEventAt < DEBOUNCE_MS) return
        lastPackage = pkg
        lastEventAt = now

        if (gatePending) return

        // Never throw the gate up during an active call. The window is time-boxed and
        // re-gating is purely event-driven (we don't proactively kick the user out), so a
        // call that outlives its access grant is left alone — the gate only reappears on the
        // next foreground change *after* the call ends. This guarantees Orbit never hangs up.
        if (isCallActive()) return

        scope.launch {
            if (blockEnforcer.shouldGate(pkg, now)) {
                gatePending = true
                launchGate(pkg)
            }
        }
    }

    /**
     * True when the device is in a phone or VoIP call. [AudioManager.MODE_IN_CALL] covers
     * cellular calls; [AudioManager.MODE_IN_COMMUNICATION] covers VoIP (WhatsApp, Meet, etc.).
     */
    private fun isCallActive(): Boolean {
        val audio = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return false
        return audio.mode == AudioManager.MODE_IN_CALL ||
            audio.mode == AudioManager.MODE_IN_COMMUNICATION
    }

    private fun launchGate(pkg: String) {
        val label = resolveLabel(applicationContext, pkg)
        val intent = QuizGateActivity.intent(applicationContext, pkg, label)
        startActivity(intent)
        // Allow subsequent gating once the gate has been shown.
        gatePending = false
    }

    private fun resolveLabel(context: Context, pkg: String): String? = runCatching {
        val pm = context.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    }.getOrNull()

    override fun onInterrupt() { /* no-op */ }

    companion object {
        private const val DEBOUNCE_MS = 800L
    }
}
