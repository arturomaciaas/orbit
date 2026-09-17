package com.orbit.blocker.data.db

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import kotlinx.coroutines.test.runTest
import org.junit.Test

class QuestionDaoTest : DaoTestBase() {

    private val dao get() = db.questionDao()

    private fun q(topic: QuizTopic, prompt: String) = Question(
        topic = topic,
        prompt = prompt,
        choices = listOf("A", "B", "C", "D"),
        correctIndex = 1,
    )

    @Test
    fun insertPreservesChoicesListViaConverter() = runTest {
        val id = dao.insert(q(QuizTopic.AWS, "What is S3?"))
        val all = dao.getAll()
        assertThat(all).hasSize(1)
        val stored = all.first { it.id == id }
        assertThat(stored.choices).containsExactly("A", "B", "C", "D").inOrder()
        assertThat(stored.correctIndex).isEqualTo(1)
    }

    @Test
    fun randomQuestions_respectsTopicFilter() = runTest {
        dao.insertAll(
            listOf(
                q(QuizTopic.AWS, "aws1"),
                q(QuizTopic.AWS, "aws2"),
                q(QuizTopic.CHESS, "chess1"),
            )
        )

        val awsOnly = dao.randomQuestions(limit = 10, topics = listOf(QuizTopic.AWS), allTopics = 0)
        assertThat(awsOnly).hasSize(2)
        assertThat(awsOnly.map { it.topic }.toSet()).containsExactly(QuizTopic.AWS)
    }

    @Test
    fun randomQuestions_allTopicsFlagDrawsFromEntireBank() = runTest {
        dao.insertAll(
            listOf(
                q(QuizTopic.AWS, "aws1"),
                q(QuizTopic.CHESS, "chess1"),
                q(QuizTopic.SYSTEM_DESIGN, "sd1"),
            )
        )

        val any = dao.randomQuestions(limit = 10, topics = emptyList(), allTopics = 1)
        assertThat(any).hasSize(3)
    }

    @Test
    fun seededCount_countsOnlySeededRows() = runTest {
        dao.insert(q(QuizTopic.AWS, "user authored"))
        dao.insert(q(QuizTopic.AWS, "seeded one").copy(seeded = true))
        assertThat(dao.count()).isEqualTo(2)
        assertThat(dao.seededCount()).isEqualTo(1)
    }
}
