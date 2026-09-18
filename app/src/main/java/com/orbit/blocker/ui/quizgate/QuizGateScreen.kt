package com.orbit.blocker.ui.quizgate

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.domain.quiz.QuizResult
import com.orbit.blocker.domain.quiz.displayName
import com.orbit.blocker.ui.components.GlassProgressBar
import com.orbit.blocker.ui.components.SpaceBackground
import com.orbit.blocker.ui.theme.AuroraGreen
import com.orbit.blocker.ui.theme.CometCyan
import com.orbit.blocker.ui.theme.GlassBorder
import com.orbit.blocker.ui.theme.GlassFill
import com.orbit.blocker.ui.theme.MeteorRed
import com.orbit.blocker.ui.theme.NebulaTeal
import com.orbit.blocker.ui.theme.NebulaViolet

/**
 * The full-screen quiz gate. Reusable: [onPassed] and [onFailed] let callers react
 * (Task 4 will grant access on pass and trigger a meteor setback on fail). In the
 * standalone Task 3 demo, callers can just show the result.
 *
 * [appLabel] is the app being unlocked, shown for context (null in the debug demo).
 */
@Composable
fun QuizGateScreen(
    appLabel: String? = null,
    viewModel: QuizGateViewModel = hiltViewModel(),
    onPassed: (QuizResult) -> Unit = {},
    onFailed: (QuizResult) -> Unit = {},
    onDismissNotEnough: () -> Unit = {},
    onQuickAccess: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.start() }

    Box(modifier = Modifier.fillMaxSize()) {
        SpaceBackground(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (val s = state) {
            QuizGateState.Loading -> CircularProgressIndicator()

            is QuizGateState.NotEnoughQuestions -> {
                Text(
                    "Not enough questions",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "You need at least ${s.required} questions in the bank but only have ${s.available}. " +
                        "Add more in the Quiz Bank.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Button(onClick = onDismissNotEnough, modifier = Modifier.padding(top = 24.dp)) {
                    Text("OK")
                }
            }

            is QuizGateState.InProgress -> QuizInProgress(
                state = s,
                appLabel = appLabel,
                onSelect = viewModel::select,
                onSubmit = viewModel::submitAnswer,
                onNext = viewModel::next,
                onQuickAccess = onQuickAccess,
            )

            is QuizGateState.Finished -> QuizFinished(
                result = s.result,
                onContinue = {
                    if (s.result.passed) onPassed(s.result) else onFailed(s.result)
                },
            )
            }
        }
    }
}

@Composable
private fun QuizInProgress(
    state: QuizGateState.InProgress,
    appLabel: String?,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onQuickAccess: () -> Unit,
) {
    // Long prompts and answers can exceed the screen height and push the action button off the
    // bottom, so the whole question scrolls. fillMaxSize + verticalScroll gives a scrollable
    // region that still lets short questions sit naturally near the top.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (appLabel != null) {
            Text(
                "Unlocking $appLabel",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            "${state.correctSoFar} of ${state.required} correct",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        GlassProgressBar(
            progress = state.correctSoFar.toFloat() / state.required,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        )
        Text(
            state.question.topic.displayName.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            state.question.prompt,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        state.question.choices.forEachIndexed { index, choice ->
            ChoiceCard(
                text = choice,
                selected = state.selectedIndex == index,
                revealed = state.revealed,
                isCorrect = index == state.correctIndex,
                isWrongPick = state.revealed && state.selectedIndex == index && index != state.correctIndex,
                // Once revealed the answer is locked, so selection taps are ignored.
                onClick = { if (!state.revealed) onSelect(index) },
            )
        }

        // Explanation is shown only after submitting a wrong answer, to teach the correct one.
        val explanation = state.question.explanation
        if (state.answeredWrong && !explanation.isNullOrBlank()) {
            Text(
                explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            )
        }

        if (!state.revealed) {
            // Phase 1: an answer is selected but not yet submitted.
            Button(
                onClick = onSubmit,
                enabled = state.selectedIndex != null,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            ) {
                Text("Submit")
            }
        } else {
            // Phase 2: answer revealed. A wrong answer keeps the gate open and serves another
            // question ("Try again") — the app only unlocks once enough answers are correct.
            val label = when {
                state.answeredWrong -> "Try again"
                state.isLastNeeded -> "Unlock"
                else -> "Next"
            }
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            ) {
                Text(label)
            }
        }

        QuickAccessButton(onQuickAccess = onQuickAccess)
    }
}

/**
 * The "1-minute quick access" bypass. Skips the quiz and grants a single minute — but at a
 * cost: it triggers a meteor strike that destroys a planet. Styled as a subdued, clearly
 * risky shortcut rather than a primary action.
 */
@Composable
private fun QuickAccessButton(onQuickAccess: () -> Unit) {
    TextButton(
        onClick = onQuickAccess,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Skip — 1 min access",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Text(
                "Triggers a meteor strike (destroys a planet)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * A single answer choice.
 *
 * Two visual phases:
 *  - Before submit ([revealed] = false): tapping selects; the selected choice shows the
 *    cyan→violet gradient.
 *  - After submit ([revealed] = true): the correct choice is highlighted green and the
 *    user's incorrect pick (if any) is highlighted red. Other choices dim to neutral.
 */
@Composable
private fun ChoiceCard(
    text: String,
    selected: Boolean,
    revealed: Boolean,
    isCorrect: Boolean,
    isWrongPick: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)

    // A choice reads "solid" (dark text on a bright fill) when it's the selected pick before
    // reveal, or when it's flagged correct/wrong after reveal.
    val solid = when {
        revealed -> isCorrect || isWrongPick
        else -> selected
    }
    val textColor by animateColorAsState(
        if (solid) Color.Black else MaterialTheme.colorScheme.onSurface,
        tween(200),
        label = "choiceText",
    )
    val fill = when {
        revealed && isCorrect ->
            Brush.horizontalGradient(listOf(AuroraGreen.copy(alpha = 0.95f), NebulaTeal.copy(alpha = 0.95f)))
        revealed && isWrongPick ->
            Brush.horizontalGradient(listOf(MeteorRed.copy(alpha = 0.95f), MeteorRed.copy(alpha = 0.80f)))
        !revealed && selected ->
            Brush.horizontalGradient(listOf(CometCyan.copy(alpha = 0.95f), NebulaViolet.copy(alpha = 0.95f)))
        else ->
            Brush.horizontalGradient(listOf(GlassFill.copy(alpha = 0.10f), GlassFill.copy(alpha = 0.06f)))
    }
    val borderColor = when {
        revealed && isCorrect -> AuroraGreen.copy(alpha = 0.7f)
        revealed && isWrongPick -> MeteorRed.copy(alpha = 0.7f)
        selected -> GlassBorder.copy(alpha = 0.5f)
        else -> GlassBorder.copy(alpha = 0.2f)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(shape)
            .background(fill, shape)
            .border(BorderStroke(1.dp, borderColor), shape)
            .selectable(selected = selected, enabled = !revealed, onClick = onClick),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun QuizFinished(result: QuizResult, onContinue: () -> Unit) {
    Text(
        if (result.passed) "Passed" else "Not quite",
        style = MaterialTheme.typography.headlineLarge,
        color = if (result.passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
    )
    Text(
        "You answered ${result.correct} of ${result.total} correctly (needed ${result.required}).",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 12.dp),
    )
    Button(onClick = onContinue, modifier = Modifier.padding(top = 24.dp)) {
        Text(if (result.passed) "Continue" else "Close")
    }
}
