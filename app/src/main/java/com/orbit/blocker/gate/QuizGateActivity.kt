package com.orbit.blocker.gate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.orbit.blocker.data.repository.AccessGrantRepository
import com.orbit.blocker.data.settings.OrbitSettings
import com.orbit.blocker.domain.gamification.GamificationEvents
import com.orbit.blocker.ui.quizgate.QuizGateScreen
import com.orbit.blocker.ui.theme.OrbitTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Full-screen gate shown over a blocked app. Launched by the AccessibilityService.
 * On pass: records a timed access grant and finishes, returning the user to the app.
 * On fail/dismiss: triggers the meteor setback (Task 7 seam) and sends the user home
 * so the blocked app is not left in the foreground.
 */
@AndroidEntryPoint
class QuizGateActivity : ComponentActivity() {

    @Inject lateinit var accessGrantRepository: AccessGrantRepository
    @Inject lateinit var settings: OrbitSettings
    @Inject lateinit var gamificationEvents: GamificationEvents

    private val blockedPackage: String by lazy {
        intent.getStringExtra(EXTRA_PACKAGE).orEmpty()
    }
    private val appLabel: String? by lazy {
        intent.getStringExtra(EXTRA_LABEL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            OrbitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuizGateScreen(
                        appLabel = appLabel,
                        onPassed = { result -> onQuizFinished(passed = true, result.wrong.size) },
                        onFailed = { result -> onQuizFinished(passed = false, result.wrong.size) },
                        onDismissNotEnough = { goHomeAndFinish() },
                        onQuickAccess = { grantQuickAccessAndFinish() },
                    )
                }
            }
        }
    }

    /**
     * Handles a graded quiz. On pass, grants the full access window and returns to the app.
     * On fail, sends the user home. In either case, getting [METEOR_WRONG_THRESHOLD] or more
     * questions wrong triggers a meteor strike — so a single miss is forgiven, but 2-of-3
     * wrong costs a planet even if the (lenient) pass threshold was still met.
     */
    private fun onQuizFinished(passed: Boolean, wrongCount: Int) {
        lifecycleScope.launch {
            if (wrongCount >= METEOR_WRONG_THRESHOLD) {
                gamificationEvents.onMeteorStrike()
            }
            if (passed) {
                if (blockedPackage.isNotEmpty()) {
                    val window = settings.accessWindowMillis.first()
                    accessGrantRepository.grantAccess(blockedPackage, window)
                }
                finish()
            } else {
                goHomeAndFinish()
            }
        }
    }

    /**
     * The 1-minute quick-access bypass: skips the quiz, grants a fixed 1-minute window, and
     * always triggers a meteor strike (destroying a planet). High convenience, high cost.
     */
    private fun grantQuickAccessAndFinish() {
        lifecycleScope.launch {
            gamificationEvents.onMeteorStrike()
            if (blockedPackage.isNotEmpty()) {
                accessGrantRepository.grantAccess(
                    blockedPackage,
                    OrbitSettings.QUICK_ACCESS_WINDOW_MILLIS,
                )
            }
            finish()
        }
    }

    private fun goHomeAndFinish() {
        val home = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(home)
        finish()
    }

    // Block the hardware back button from dismissing the gate into the blocked app.
    @Deprecated("Intentionally trap back to prevent bypass")
    override fun onBackPressed() {
        goHomeAndFinish()
    }

    companion object {
        private const val EXTRA_PACKAGE = "extra_package"
        private const val EXTRA_LABEL = "extra_label"

        /** Wrong answers at/above this count trigger a meteor strike (2 of 3 wrong). */
        private const val METEOR_WRONG_THRESHOLD = 2

        fun intent(context: Context, packageName: String, label: String?): Intent =
            Intent(context, QuizGateActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE, packageName)
                putExtra(EXTRA_LABEL, label)
                // Launch over the blocked app as a fresh task.
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NO_ANIMATION
            }
    }
}
