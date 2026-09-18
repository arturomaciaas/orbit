package com.orbit.blocker.data.repository

import com.orbit.blocker.data.db.QuestionDao
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Manages the local quiz bank (Task 3). */
interface QuestionRepository {
    fun observeAll(): Flow<List<Question>>
    fun observeByTopic(topic: QuizTopic): Flow<List<Question>>
    suspend fun add(question: Question): Long
    suspend fun update(question: Question)
    suspend fun delete(question: Question)
    suspend fun count(): Int
    suspend fun seededCount(): Int
    suspend fun addAll(questions: List<Question>)

    /** Seeded questions that carry a [Question.sourceKey] (i.e. came from the bundled asset). */
    suspend fun seededWithSource(): List<Question>

    /** Removes seeded questions whose source key is in [keys]. User-authored rows are untouched. */
    suspend fun deleteBySourceKeys(keys: List<String>)

    /** Removes legacy shipped questions (seeded, no source key) from pre-sync installs. */
    suspend fun deleteLegacyKeylessSeeded()

    /** Inserts or replaces questions, keyed by their unique [Question.sourceKey]. */
    suspend fun upsertAll(questions: List<Question>)

    /** Draws [limit] random questions across [topics] (empty = all topics). */
    suspend fun randomQuestions(limit: Int, topics: List<QuizTopic>): List<Question>
}

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
) : QuestionRepository {

    override fun observeAll(): Flow<List<Question>> = questionDao.observeAll()
    override fun observeByTopic(topic: QuizTopic): Flow<List<Question>> =
        questionDao.observeByTopic(topic)

    override suspend fun add(question: Question): Long = questionDao.insert(question)
    override suspend fun update(question: Question) = questionDao.update(question)
    override suspend fun delete(question: Question) = questionDao.delete(question)
    override suspend fun count(): Int = questionDao.count()
    override suspend fun seededCount(): Int = questionDao.seededCount()
    override suspend fun addAll(questions: List<Question>) = questionDao.insertAll(questions)

    override suspend fun seededWithSource(): List<Question> = questionDao.getSeededWithSource()

    override suspend fun deleteBySourceKeys(keys: List<String>) {
        if (keys.isNotEmpty()) questionDao.deleteBySourceKeys(keys)
    }

    override suspend fun deleteLegacyKeylessSeeded() = questionDao.deleteLegacyKeylessSeeded()

    override suspend fun upsertAll(questions: List<Question>) = questionDao.insertAll(questions)

    override suspend fun randomQuestions(limit: Int, topics: List<QuizTopic>): List<Question> =
        questionDao.randomQuestions(
            limit = limit,
            topics = topics,
            allTopics = if (topics.isEmpty()) 1 else 0,
        )
}
