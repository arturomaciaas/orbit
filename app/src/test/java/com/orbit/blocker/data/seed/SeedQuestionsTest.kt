package com.orbit.blocker.data.seed

import com.google.common.truth.Truth.assertThat
import com.orbit.blocker.data.model.QuizTopic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File

/**
 * Validates the bundled `assets/seed_questions.json` starter bank. Parses the asset straight
 * from the module sources so it stays a fast JVM test (no Android/Robolectric context needed).
 */
class SeedQuestionsTest {

    @Serializable
    private data class SeedBank(val version: Int = 1, val questions: List<SeedQuestionDto> = emptyList())

    @Serializable
    private data class SeedQuestionDto(
        val key: String,
        val topic: String,
        val prompt: String,
        val choices: List<String>,
        val correctIndex: Int,
        val explanation: String? = null,
    )

    private val all: List<SeedQuestionDto> = run {
        val file = File("src/main/assets/seed_questions.json")
        assertThat(file.exists()).isTrue()
        Json { ignoreUnknownKeys = true }.decodeFromString<SeedBank>(file.readText()).questions
    }

    @Test
    fun everyTopicHasQuestions() {
        val topics = all.map { QuizTopic.valueOf(it.topic) }.toSet()
        assertThat(topics).containsExactlyElementsIn(QuizTopic.entries)
    }

    @Test
    fun everySeedQuestionIsWellFormed() {
        all.forEach { q ->
            assertThat(q.choices.size).isAtLeast(2)
            assertThat(q.correctIndex).isIn(q.choices.indices.toList())
            assertThat(q.prompt).isNotEmpty()
            assertThat(q.key).isNotEmpty()
            assertThat(q.choices.none { it.isBlank() }).isTrue()
            // Topic string must map to a known enum value.
            assertThat(QuizTopic.entries.any { it.name == q.topic }).isTrue()
        }
    }

    @Test
    fun everyKeyIsUnique() {
        val keys = all.map { it.key }
        assertThat(keys).containsNoDuplicates()
    }

    @Test
    fun hasReasonableStarterVolume() {
        // At least a handful per topic so the default 3-question quiz always has a pool.
        QuizTopic.entries.forEach { topic ->
            assertThat(all.count { it.topic == topic.name }).isAtLeast(4)
        }
    }
}
