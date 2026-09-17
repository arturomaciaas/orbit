package com.orbit.blocker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orbit.blocker.data.model.Question
import com.orbit.blocker.data.model.QuizTopic
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Update
    suspend fun update(question: Question)

    @Delete
    suspend fun delete(question: Question)

    @Query("SELECT * FROM questions ORDER BY id DESC")
    fun observeAll(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE topic = :topic ORDER BY id DESC")
    fun observeByTopic(topic: QuizTopic): Flow<List<Question>>

    @Query("SELECT * FROM questions")
    suspend fun getAll(): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE seeded = 1")
    suspend fun seededCount(): Int

    /**
     * Returns up to [limit] random questions, optionally restricted to [topics].
     * When [topics] is empty, draws from the whole bank.
     */
    @Query(
        """
        SELECT * FROM questions
        WHERE (:allTopics = 1 OR topic IN (:topics))
        ORDER BY RANDOM()
        LIMIT :limit
        """
    )
    suspend fun randomQuestions(
        limit: Int,
        topics: List<QuizTopic>,
        allTopics: Int,
    ): List<Question>
}
