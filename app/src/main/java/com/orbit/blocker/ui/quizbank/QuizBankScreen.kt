package com.orbit.blocker.ui.quizbank

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import com.orbit.blocker.domain.quiz.displayName
import com.orbit.blocker.ui.components.GlassCard

@Composable
fun QuizBankScreen(viewModel: QuizBankViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<EditorTarget?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Quiz Bank", style = MaterialTheme.typography.headlineLarge)
            Text(
                "${state.questions.size} questions",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp),
            ) {
                item {
                    FilterChip(
                        selected = state.topicFilter == null,
                        onClick = { viewModel.setTopicFilter(null) },
                        label = { Text("All") },
                    )
                }
                items(QuizTopic.entries.toList()) { topic ->
                    val count = state.countByTopic[topic] ?: 0
                    FilterChip(
                        selected = state.topicFilter == topic,
                        onClick = { viewModel.setTopicFilter(topic) },
                        label = { Text("${topic.displayName} ($count)") },
                    )
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.visibleQuestions, key = { it.id }) { question ->
                    QuestionCard(
                        question = question,
                        onEdit = { editing = EditorTarget.Edit(question) },
                        onDelete = { viewModel.delete(question) },
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { editing = EditorTarget.New },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add question")
        }
    }

    editing?.let { target ->
        QuestionEditorDialog(
            initial = (target as? EditorTarget.Edit)?.question,
            onDismiss = { editing = null },
            onSave = {
                viewModel.save(it)
                editing = null
            },
        )
    }
}

private sealed interface EditorTarget {
    data object New : EditorTarget
    data class Edit(val question: Question) : EditorTarget
}

@Composable
private fun QuestionCard(
    question: Question,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    question.topic.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
            Text(question.prompt, style = MaterialTheme.typography.bodyLarge)
            val answer = question.choices.getOrNull(question.correctIndex)
            if (answer != null) {
                Text(
                    "Answer: $answer",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
