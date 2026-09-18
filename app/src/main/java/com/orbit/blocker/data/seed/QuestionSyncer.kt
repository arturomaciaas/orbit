package com.orbit.blocker.data.seed

import android.content.Context
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import com.orbit.blocker.data.repository.QuestionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads the bundled `assets/seed_questions.json` bank and reconciles it into the local
 * question table on every launch. Unlike a one-time seed, this keeps the shipped bank in
 * sync as questions are added, edited, or removed in the asset across app updates:
 *
 *  - **Add**: a question whose [SeedQuestionDto.key] isn't in the DB is inserted.
 *  - **Update**: a question whose key exists but whose content changed is replaced
 *    (upsert keyed on [Question.sourceKey]), so fixing a typo doesn't drop user history.
 *  - **Remove**: a seeded question whose key is no longer in the asset is deleted.
 *
 * User-authored questions (created in the Quiz Bank screen, [Question.sourceKey] == null)
 * are never touched by this sync.
 *
 * Failures are swallowed so a malformed or missing asset can never crash startup; the app
 * simply runs with whatever bank is already present.
 */
@Singleton
class QuestionSyncer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val questionRepository: QuestionRepository,
) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sync() {
        val bundled = runCatching { loadBundled() }.getOrNull() ?: return
        val incomingKeys = bundled.mapTo(HashSet()) { it.key }

        // One-time cleanup for installs seeded before sourceKey existed: those shipped rows are
        // seeded = true with sourceKey = null. Drop them so the keyed versions below replace them
        // rather than duplicate. User-authored rows (seeded = false) are left untouched.
        questionRepository.deleteLegacyKeylessSeeded()

        // Remove: previously-synced questions whose key no longer appears in the asset.
        val staleKeys = questionRepository.seededWithSource()
            .mapNotNull { it.sourceKey }
            .filter { it !in incomingKeys }
        questionRepository.deleteBySourceKeys(staleKeys)

        // Add + update: upsert every incoming question, keyed on the unique sourceKey index.
        // REPLACE conflict strategy means edited content overwrites the prior row in place.
        questionRepository.upsertAll(bundled.map { it.toQuestion() })
    }

    private fun loadBundled(): List<SeedQuestionDto> {
        val text = context.assets.open(ASSET_NAME)
            .bufferedReader()
            .use { it.readText() }
        val bank = json.decodeFromString<SeedBank>(text)
        // Guard against malformed entries so a bad row can't crash grading later.
        return bank.questions.filter { it.isValid() }
    }

    private fun SeedQuestionDto.toQuestion(): Question = Question(
        topic = QuizTopic.valueOf(topic),
        prompt = prompt,
        choices = choices,
        correctIndex = correctIndex,
        explanation = explanation,
        seeded = true,
        sourceKey = key,
    )

    private fun SeedQuestionDto.isValid(): Boolean {
        val topicValid = QuizTopic.entries.any { it.name == topic }
        val indexValid = correctIndex in choices.indices
        return key.isNotBlank() && prompt.isNotBlank() && choices.size >= 2 && topicValid && indexValid
    }

    private companion object {
        const val ASSET_NAME = "seed_questions.json"
    }

    @Serializable
    private data class SeedBank(
        @SerialName("version") val version: Int = 1,
        @SerialName("questions") val questions: List<SeedQuestionDto> = emptyList(),
    )

    @Serializable
    private data class SeedQuestionDto(
        val key: String,
        val topic: String,
        val prompt: String,
        val choices: List<String>,
        val correctIndex: Int,
        val explanation: String? = null,
    )
}
