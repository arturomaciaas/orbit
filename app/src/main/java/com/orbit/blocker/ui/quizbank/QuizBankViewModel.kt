package com.orbit.blocker.ui.quizbank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import com.orbit.blocker.data.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizBankUiState(
    val questions: List<Question> = emptyList(),
    val topicFilter: QuizTopic? = null,
) {
    val visibleQuestions: List<Question>
        get() = topicFilter?.let { t -> questions.filter { it.topic == t } } ?: questions

    val countByTopic: Map<QuizTopic, Int>
        get() = questions.groupingBy { it.topic }.eachCount()
}

@HiltViewModel
class QuizBankViewModel @Inject constructor(
    private val repository: QuestionRepository,
) : ViewModel() {

    private val topicFilter = MutableStateFlow<QuizTopic?>(null)

    val uiState: StateFlow<QuizBankUiState> = combine(
        repository.observeAll(),
        topicFilter,
    ) { questions, filter ->
        QuizBankUiState(questions = questions, topicFilter = filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = QuizBankUiState(),
    )

    fun setTopicFilter(topic: QuizTopic?) {
        topicFilter.value = topic
    }

    fun save(question: Question) {
        viewModelScope.launch {
            if (question.id == 0L) repository.add(question) else repository.update(question)
        }
    }

    fun delete(question: Question) {
        viewModelScope.launch { repository.delete(question) }
    }
}
