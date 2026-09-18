package com.orbit.blocker.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.orbit.blocker.domain.block.BlockEnforcer
import com.orbit.blocker.domain.block.CallStateGate
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
    private val callStateGate = CallStateGate()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Debounce: ignore the same package within this window, and while a gate is pending.
    private var lastPackage: String? = null
    private var lastEventAt: Long = 0L
    @Volatile private var gatePending: Boolean = false

    // Packages of enabled input methods (keyboards like Gboard). Their windows fire
    // TYPE_WINDOW_STATE_CHANGED with the IME's own package, which must NEVER be gated — a
    // keyboard popping up over an allowed app (e.g. tapping WhatsApp's text field) is not an
    // app switch. Cached because the enabled-IME set rarely changes; refreshed on connect.
    @Volatile private var imePackages: Set<String> = emptySet()

    override fun onServiceConnected() {
        super.onServiceConnected()
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ServiceEntryPoint::class.java,
        )
        blockEnforcer = entryPoint.blockEnforcer()
        blockEnforcer.startObserving()
        imePackages = loadInputMethodPackages()
    }

    /** Package names of every enabled input method (keyboard) on the device. */
    private fun loadInputMethodPackages(): Set<String> = runCatching {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            ?: return@runCatching emptySet()
        imm.enabledInputMethodList
            .mapNotNull { it.packageName }
            .toSet()
    }.getOrDefault(emptySet())

    /**
     * True if [pkg] is an enabled input method. If the cached set is empty (e.g. the IME
     * framework wasn't ready at connect time), refresh once before deciding so a keyboard is
     * never gated on a cold cache.
     */
    private fun isInputMethod(pkg: String): Boolean {
        if (imePackages.isEmpty()) imePackages = loadInputMethodPackages()
        return pkg in imePackages
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        // Skip our own app so the gate/back-to-home flow doesn't loop.
        if (pkg == packageName) return

        // Skip input methods (keyboards). An IME window opening over the current app fires a
        // window-state-change carrying the IME's package; that is not an app switch and must
        // never be gated. Lazily (re)load the set in case it was empty when the service
        // connected or the user enabled a new keyboard since.
        if (isInputMethod(pkg)) return

        val now = System.currentTimeMillis()
        if (pkg == lastPackage && now - lastEventAt < DEBOUNCE_MS) return
        lastPackage = pkg
        lastEventAt = now

        if (gatePending) return

        // Never throw the gate up during an active call — it could hang up a phone call or
        // disrupt a VoIP call. The audio communication mode covers both cellular and VoIP, but
        // VoIP apps can leave it stuck; [CallStateGate] bounds how long a standalone
        // communication-mode signal is trusted so a stuck mode can't disable blocking forever.
        if (callStateGate.shouldSuppressGate(
                audioInCommunication = isAudioInCommunication(),
                now = now,
            )
        ) {
            return
        }

        scope.launch {
            if (blockEnforcer.shouldGate(pkg, now)) {
                gatePending = true
                launchGate(pkg)
            }
        }
    }

    /**
     * True when the audio system is in a call/communication mode. [AudioManager.MODE_IN_CALL]
     * covers cellular calls; [AudioManager.MODE_IN_COMMUNICATION] covers VoIP (WhatsApp, Meet,
     * etc.). Unreliable on its own (VoIP apps can leave it stuck), so [CallStateGate] bounds how
     * long it is trusted.
     */
    private fun isAudioInCommunication(): Boolean {
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
