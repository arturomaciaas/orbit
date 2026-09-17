package com.orbit.blocker.domain.quiz

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import org.junit.Test

class QuizSessionTest {

    private fun question(id: Long, correct: Int) = Question(
        id = id,
        topic = QuizTopic.AWS,
        prompt = "q$id",
        choices = listOf("a", "b", "c", "d"),
        correctIndex = correct,
    )

    @Test
    fun passes_whenCorrectMeetsRequired() {
        val session = QuizSession(
            questions = listOf(question(1, 0), question(2, 1), question(3, 2)),
            required = 2,
        )
        session.select(0); session.next()   // correct
        session.select(1); session.next()   // correct
        session.select(0)                    // wrong

        val result = session.grade()
        assertThat(result.correct).isEqualTo(2)
        assertThat(result.passed).isTrue()
    }

    @Test
    fun fails_whenCorrectBelowRequired() {
        val session = QuizSession(
            questions = listOf(question(1, 0), question(2, 1)),
            required = 2,
        )
        session.select(0); session.next()  // correct
        session.select(0)                   // wrong

        val result = session.grade()
        assertThat(result.correct).isEqualTo(1)
        assertThat(result.passed).isFalse()
        assertThat(result.wrong).hasSize(1)
        assertThat(result.wrong.first().question.id).isEqualTo(2)
    }

    @Test
    fun unansweredQuestionsCountAsWrong() {
        val session = QuizSession(listOf(question(1, 0), question(2, 0)), required = 1)
        session.select(0) // answer only the first, never advance/answer second

        val result = session.grade()
        assertThat(result.total).isEqualTo(2)
        assertThat(result.correct).isEqualTo(1)
        // Second question has selectedIndex -1 => wrong.
        assertThat(result.wrong.single().selectedIndex).isEqualTo(-1)
    }

    @Test
    fun navigationTracksCurrentAndLast() {
        val session = QuizSession(listOf(question(1, 0), question(2, 0), question(3, 0)), required = 1)
        assertThat(session.currentIndex).isEqualTo(0)
        assertThat(session.isLast).isFalse()
        session.next(); session.next()
        assertThat(session.currentIndex).isEqualTo(2)
        assertThat(session.isLast).isTrue()
        assertThat(session.next()).isFalse() // cannot advance past last
        session.previous()
        assertThat(session.currentIndex).isEqualTo(1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsRequiredGreaterThanQuestionCount() {
        QuizSession(listOf(question(1, 0)), required = 2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyQuestions() {
        QuizSession(emptyList(), required = 1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidChoiceIndex() {
        val session = QuizSession(listOf(question(1, 0)), required = 1)
        session.select(99)
    }
}
