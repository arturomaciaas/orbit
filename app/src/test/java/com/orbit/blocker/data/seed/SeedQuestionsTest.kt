package com.orbit.blocker.data.seed

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.QuizTopic
import org.junit.Test

class SeedQuestionsTest {

    private val all = SeedQuestions.all()

    @Test
    fun everyTopicHasQuestions() {
        val topics = all.map { it.topic }.toSet()
        assertThat(topics).containsExactlyElementsIn(QuizTopic.entries)
    }

    @Test
    fun everySeedQuestionIsWellFormed() {
        all.forEach { q ->
            assertThat(q.choices.size).isAtLeast(2)
            assertThat(q.correctIndex).isIn(q.choices.indices.toList())
            assertThat(q.prompt).isNotEmpty()
            assertThat(q.choices.none { it.isBlank() }).isTrue()
            assertThat(q.seeded).isTrue()
        }
    }

    @Test
    fun hasReasonableStarterVolume() {
        // At least a handful per topic so the default 3-question quiz always has a pool.
        QuizTopic.entries.forEach { topic ->
            assertThat(all.count { it.topic == topic }).isAtLeast(4)
        }
    }
}
