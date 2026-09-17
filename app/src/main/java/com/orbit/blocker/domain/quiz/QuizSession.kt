package com.orbit.blocker.domain.quiz

import com.orbit.blocker.data.model.Question

/**
 * The outcome of grading a single question within a quiz session.
 */
data class GradedAnswer(
    val question: Question,
    val selectedIndex: Int,
) {
    val isCorrect: Boolean get() = selectedIndex == question.correctIndex
}

/**
 * The immutable result of a completed quiz. [passed] is true only when the number
 * of correct answers meets or exceeds [required].
 */
data class QuizResult(
    val graded: List<GradedAnswer>,
    val required: Int,
) {
    val total: Int get() = graded.size
    val correct: Int get() = graded.count { it.isCorrect }
    val passed: Boolean get() = correct >= required
    /** Questions answered incorrectly, useful for the meteor-setback wiring (Task 7). */
    val wrong: List<GradedAnswer> get() = graded.filterNot { it.isCorrect }
}

/**
 * Pure state machine for a quiz gate. Given an ordered list of [questions] and the
 * number [required] to pass, tracks the current index and selected answers, and can
 * produce a [QuizResult]. No Android dependencies, fully unit-testable.
 */
class QuizSession(
    val questions: List<Question>,
    val required: Int,
) {
    init {
        require(questions.isNotEmpty()) { "A quiz session needs at least one question" }
        require(required in 1..questions.size) {
            "required ($required) must be between 1 and question count (${questions.size})"
        }
    }

    private val selections = HashMap<Int, Int>()
    var currentIndex: Int = 0
        private set

    val currentQuestion: Question get() = questions[currentIndex]
    val isLast: Boolean get() = currentIndex == questions.lastIndex
    val answeredCount: Int get() = selections.size

    /** Records the selected choice index for the current question. */
    fun select(choiceIndex: Int) {
        require(choiceIndex in currentQuestion.choices.indices) { "Invalid choice index" }
        selections[currentIndex] = choiceIndex
    }

    fun selectedForCurrent(): Int? = selections[currentIndex]

    /** Advances to the next question if possible. Returns true if it moved. */
    fun next(): Boolean {
        if (isLast) return false
        currentIndex++
        return true
    }

    fun previous(): Boolean {
        if (currentIndex == 0) return false
        currentIndex--
        return true
    }

    /** True when every question has a recorded selection. */
    fun allAnswered(): Boolean = selections.size == questions.size

    /**
     * Grades the session. Any unanswered question is treated as incorrect
     * (selectedIndex = -1), so grading is always well-defined.
     */
    fun grade(): QuizResult {
        val graded = questions.mapIndexed { index, question ->
            GradedAnswer(question = question, selectedIndex = selections[index] ?: -1)
        }
        return QuizResult(graded = graded, required = required)
    }
}
