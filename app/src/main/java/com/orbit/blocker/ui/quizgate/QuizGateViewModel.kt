package com.orbit.blocker.ui.quizgate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.repository.QuestionRepository
import com.orbit.blocker.data.settings.OrbitSettings
import com.orbit.blocker.domain.quiz.GradedAnswer
import com.orbit.blocker.domain.quiz.QuizResult
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
        /** How many questions have been answered correctly so far. */
        val correctSoFar: Int,
        /** How many correct answers are needed to unlock the app. */
        val required: Int,
        val selectedIndex: Int?,
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
        /**
         * The just-submitted correct answer completes the gate. [correctSoFar] already counts
         * the current answer (submit increments it before this state is emitted), so the gate
         * is done when it has reached [required] on a correct reveal.
         */
        val isLastNeeded: Boolean get() = revealed && !answeredWrong && correctSoFar >= required
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

    /** The pool the gate draws from; used to keep serving fresh questions on a wrong answer. */
    private var pool: List<Question> = emptyList()
    private var required: Int = 0

    /**
     * IDs of the most recently served questions, newest last. Used to avoid re-showing a
     * question too soon (a real problem on retries, where the same one kept reappearing).
     */
    private val recentlyServed = ArrayDeque<Long>()

    private var currentQuestion: Question? = null
    private var selectedIndex: Int? = null
    private var revealed: Boolean = false

    /** How many the user has gotten right so far — the gate opens once this reaches [required]. */
    private var correctSoFar: Int = 0
    /**
     * Every answer graded during this gate session, including retries. Used to build the final
     * [QuizResult] so downstream logic (e.g. meteor strike on too many wrong answers) still works.
     */
    private val gradedAnswers = mutableListOf<GradedAnswer>()

    /** Loads a fresh quiz. Call once when the gate opens. */
    fun start() {
        viewModelScope.launch {
            required = settings.questionsRequired.first()
            pool = questionRepository.observeAll().first()
            if (pool.size < required) {
                _state.value = QuizGateState.NotEnoughQuestions(pool.size, required)
                return@launch
            }
            correctSoFar = 0
            gradedAnswers.clear()
            recentlyServed.clear()
            serveNext()
        }
    }

    /** Records a choice for the current question. No-op once the answer has been submitted. */
    fun select(choiceIndex: Int) {
        if (currentQuestion == null || revealed) return
        selectedIndex = choiceIndex
        emitInProgress()
    }

    /**
     * Submits the current answer, revealing whether it was correct. Choices lock and the
     * explanation is shown for a wrong answer. Requires a selection; otherwise a no-op.
     */
    fun submitAnswer() {
        val question = currentQuestion ?: return
        val picked = selectedIndex ?: return
        revealed = true
        gradedAnswers += GradedAnswer(question = question, selectedIndex = picked)
        if (picked == question.correctIndex) correctSoFar++
        emitInProgress()
    }

    /**
     * Moves forward after an answer is revealed. A correct answer that reaches [required]
     * finishes the gate with a pass; otherwise (correct-but-not-done, or wrong) it serves
     * another question — the gate never ends in failure, it just keeps quizzing until the
     * user has enough correct answers.
     */
    fun next() {
        val question = currentQuestion ?: return
        if (!revealed) return
        val wasCorrect = selectedIndex == question.correctIndex
        if (wasCorrect && correctSoFar >= required) {
            _state.value = QuizGateState.Finished(
                QuizResult(graded = gradedAnswers.toList(), required = required),
            )
            return
        }
        serveNext()
    }

    /**
     * Picks the next question at random, avoiding any of the [recentlyServed] ones so questions
     * don't visibly repeat back-to-back. The "avoid recent" window scales with the pool so it
     * can never exclude every candidate (with a tiny pool we simply allow repeats).
     */
    private fun serveNext() {
        val next = pickNextQuestion()
        currentQuestion = next
        if (next != null) rememberServed(next.id)
        selectedIndex = null
        revealed = false
        emitInProgress()
    }

    private fun pickNextQuestion(): Question? {
        if (pool.isEmpty()) return null
        // Keep at most half the pool "recent" so there are always fresh candidates to choose from.
        val avoidCount = ((pool.size - 1) / 2).coerceAtMost(recentlyServed.size)
        val avoid = recentlyServed.toList().takeLast(avoidCount).toSet()
        val candidates = pool.filter { it.id !in avoid }.ifEmpty { pool }
        return candidates.random()
    }

    private fun rememberServed(id: Long) {
        recentlyServed.addLast(id)
        val maxWindow = (pool.size - 1).coerceAtLeast(0)
        while (recentlyServed.size > maxWindow) recentlyServed.removeFirst()
    }

    private fun emitInProgress() {
        val question = currentQuestion ?: return
        _state.value = QuizGateState.InProgress(
            question = question,
            correctSoFar = correctSoFar,
            required = required,
            selectedIndex = selectedIndex,
            revealed = revealed,
        )
    }
}
