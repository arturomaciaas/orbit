package com.orbit.blocker.domain.quiz

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import org.junit.Test
import kotlin.random.Random

class QuestionSelectorTest {

    private fun q(id: Long, topic: QuizTopic) = Question(
        id = id,
        topic = topic,
        prompt = "q$id",
        choices = listOf("a", "b"),
        correctIndex = 0,
    )

    @Test
    fun returnsRequestedCount() {
        val pool = (1..20L).map { q(it, QuizTopic.entries[(it % 4).toInt()]) }
        val picked = QuestionSelector.pickBalanced(pool, count = 5, random = Random(42))
        assertThat(picked).hasSize(5)
    }

    @Test
    fun returnsAllWhenPoolSmallerThanCount() {
        val pool = listOf(q(1, QuizTopic.AWS), q(2, QuizTopic.CHESS))
        val picked = QuestionSelector.pickBalanced(pool, count = 5, random = Random(1))
        assertThat(picked).hasSize(2)
    }

    @Test
    fun spreadsAcrossTopicsWhenPossible() {
        // 4 topics, 3 each. Asking for 4 should hit all 4 distinct topics (round-robin).
        val pool = QuizTopic.entries.flatMap { topic ->
            (1..3).map { q((topic.ordinal * 10 + it).toLong(), topic) }
        }
        val picked = QuestionSelector.pickBalanced(pool, count = 4, random = Random(7))
        assertThat(picked.map { it.topic }.toSet()).hasSize(4)
    }

    @Test
    fun emptyPoolOrZeroCountReturnsEmpty() {
        assertThat(QuestionSelector.pickBalanced(emptyList(), 3)).isEmpty()
        assertThat(QuestionSelector.pickBalanced(listOf(q(1, QuizTopic.AWS)), 0)).isEmpty()
    }

    @Test
    fun deterministicWithSeededRandom() {
        val pool = (1..12L).map { q(it, QuizTopic.entries[(it % 4).toInt()]) }
        val a = QuestionSelector.pickBalanced(pool, 5, Random(99)).map { it.id }
        val b = QuestionSelector.pickBalanced(pool, 5, Random(99)).map { it.id }
        assertThat(a).isEqualTo(b)
    }
}
