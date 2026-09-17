package com.orbit.blocker.ui.quizbank

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import com.orbit.blocker.domain.quiz.displayName

/**
 * Add/edit dialog for a quiz question. Enforces exactly one selected correct answer
 * and non-blank prompt + choices before allowing save.
 */
@Composable
fun QuestionEditorDialog(
    initial: Question?,
    onDismiss: () -> Unit,
    onSave: (Question) -> Unit,
) {
    var topic by remember { mutableStateOf(initial?.topic ?: QuizTopic.SYSTEM_DESIGN) }
    var prompt by remember { mutableStateOf(initial?.prompt ?: "") }
    val choices = remember {
        mutableStateListOf<String>().apply {
            val existing = initial?.choices
            if (existing != null && existing.size >= 2) addAll(existing)
            else addAll(listOf("", "", "", ""))
        }
    }
    var correctIndex by remember { mutableStateOf(initial?.correctIndex ?: 0) }
    var explanation by remember { mutableStateOf(initial?.explanation ?: "") }

    val nonBlankChoices = choices.count { it.isNotBlank() }
    val valid = prompt.isNotBlank() &&
        nonBlankChoices >= 2 &&
        correctIndex in choices.indices &&
        choices[correctIndex].isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = {
                    // Compact out blank trailing choices while keeping correctIndex valid.
                    val kept = choices.mapIndexedNotNull { i, c ->
                        if (c.isNotBlank()) i to c else null
                    }
                    val keptChoices = kept.map { it.second }
                    val newCorrect = kept.indexOfFirst { it.first == correctIndex }.coerceAtLeast(0)
                    onSave(
                        (initial ?: Question(topic = topic, prompt = "", choices = emptyList(), correctIndex = 0))
                            .copy(
                                topic = topic,
                                prompt = prompt.trim(),
                                choices = keptChoices,
                                correctIndex = newCorrect,
                                explanation = explanation.ifBlank { null },
                            )
                    )
                },
            ) { Text(if (initial == null) "Add" else "Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(if (initial == null) "New question" else "Edit question") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Topic", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuizTopic.entries.forEach { t ->
                        FilterChip(
                            selected = topic == t,
                            onClick = { topic = t },
                            label = { Text(t.displayName) },
                        )
                    }
                }

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Choices (tap the circle to mark the correct one)", style = MaterialTheme.typography.labelLarge)
                choices.forEachIndexed { index, value ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = correctIndex == index,
                            onClick = { correctIndex = index },
                        )
                        OutlinedTextField(
                            value = value,
                            onValueChange = { choices[index] = it },
                            label = { Text("Choice ${index + 1}") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Explanation (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
