package com.orbit.blocker.ui.quizgate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.repository.QuestionRepository
import com.orbit.blocker.data.settings.OrbitSettings
import com.orbit.blocker.domain.quiz.QuestionSelector
import com.orbit.blocker.domain.quiz.QuizResult
import com.orbit.blocker.domain.quiz.QuizSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the quiz gate. */
sealed interface QuizGateState {
    data object Loading : QuizGateState
    data class NotEnoughQuestions(val available: Int, val required: Int) : QuizGateState
    data class InProgress(
        val question: Question,
        val index: Int,
        val total: Int,
        val required: Int,
        val selectedIndex: Int?,
        val isLast: Boolean,
    ) : QuizGateState
    data class Finished(val result: QuizResult) : QuizGateState
}

@HiltViewModel
class QuizGateViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val settings: OrbitSettings,
) : ViewModel() {

    private val _state = MutableStateFlow<QuizGateState>(QuizGateState.Loading)
    val state: StateFlow<QuizGateState> = _state.asStateFlow()

    private var session: QuizSession? = null

    /** Loads a fresh quiz. Call once when the gate opens. */
    fun start() {
        viewModelScope.launch {
            val required = settings.questionsRequired.first()
            val pool = questionRepository.observeAll().first()
            if (pool.size < required) {
                _state.value = QuizGateState.NotEnoughQuestions(pool.size, required)
                return@launch
            }
            // Pull a balanced set sized to the requirement (a small buffer could be added later).
            val picked = QuestionSelector.pickBalanced(pool, required)
            session = QuizSession(picked, required)
            emitInProgress()
        }
    }

    fun select(choiceIndex: Int) {
        val s = session ?: return
        s.select(choiceIndex)
        emitInProgress()
    }

    fun next() {
        val s = session ?: return
        if (s.isLast) {
            _state.value = QuizGateState.Finished(s.grade())
        } else {
            s.next()
            emitInProgress()
        }
    }

    private fun emitInProgress() {
        val s = session ?: return
        _state.value = QuizGateState.InProgress(
            question = s.currentQuestion,
            index = s.currentIndex,
            total = s.questions.size,
            required = s.required,
            selectedIndex = s.selectedForCurrent(),
            isLast = s.isLast,
        )
    }
}
