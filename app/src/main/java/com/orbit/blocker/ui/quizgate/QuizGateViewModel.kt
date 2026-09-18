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
        /**
         * True once the user has submitted their answer for this question. While revealed,
         * choices are locked and colored correct/incorrect, and the explanation is shown for
         * a wrong answer.
         */
        val revealed: Boolean = false,
    ) : QuizGateState {
        val correctIndex: Int get() = question.correctIndex
        /** The submitted answer was wrong (only meaningful once [revealed]). */
        val answeredWrong: Boolean get() = revealed && selectedIndex != correctIndex
    }
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

    /** Indices of questions whose answer has been submitted (revealed + locked). */
    private val revealedIndices = mutableSetOf<Int>()

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
            revealedIndices.clear()
            emitInProgress()
        }
    }

    /** Records a choice for the current question. No-op once the answer has been submitted. */
    fun select(choiceIndex: Int) {
        val s = session ?: return
        if (s.currentIndex in revealedIndices) return // locked after submit
        s.select(choiceIndex)
        emitInProgress()
    }

    /**
     * Submits the current answer, revealing whether it was correct. Choices lock and the
     * explanation is shown for a wrong answer. Requires a selection; otherwise a no-op.
     */
    fun submitAnswer() {
        val s = session ?: return
        if (s.selectedForCurrent() == null) return
        revealedIndices += s.currentIndex
        emitInProgress()
    }

    /** Advances to the next question, or finishes and grades the whole session on the last one. */
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
            revealed = s.currentIndex in revealedIndices,
        )
    }
}
