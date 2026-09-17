package com.orbit.blocker.ui.quizgate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.domain.quiz.QuizResult
import com.orbit.blocker.domain.quiz.displayName

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
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.start() }

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
                onNext = viewModel::next,
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

@Composable
private fun QuizInProgress(
    state: QuizGateState.InProgress,
    appLabel: String?,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (appLabel != null) {
            Text(
                "Unlocking $appLabel",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            "Question ${state.index + 1} of ${state.total}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        LinearProgressIndicator(
            progress = { (state.index + 1f) / state.total },
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        )
        Text(
            state.question.topic.displayName,
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
            val selected = state.selectedIndex == index
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .selectable(selected = selected, onClick = { onSelect(index) }),
            ) {
                Text(
                    choice,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        Button(
            onClick = onNext,
            enabled = state.selectedIndex != null,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        ) {
            Text(if (state.isLast) "Submit" else "Next")
        }
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
