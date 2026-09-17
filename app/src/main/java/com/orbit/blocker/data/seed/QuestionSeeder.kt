package com.orbit.blocker.data.seed

import com.orbit.blocker.data.repository.QuestionRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Populates the question bank with [SeedQuestions] the first time the app runs
 * (i.e. when the bank is empty). Idempotent: does nothing if any question exists.
 */
@Singleton
class QuestionSeeder @Inject constructor(
    private val questionRepository: QuestionRepository,
) {
    suspend fun seedIfEmpty() {
        if (questionRepository.count() == 0) {
            questionRepository.addAll(SeedQuestions.all())
        }
    }
}
