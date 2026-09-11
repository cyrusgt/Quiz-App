package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_questions ORDER BY id DESC")
    fun getAllQuestions(): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions WHERE category = :category ORDER BY RANDOM()")
    suspend fun getQuestionsByCategory(category: String): List<QuizQuestionEntity>

    @Query("SELECT * FROM quiz_questions ORDER BY RANDOM()")
    suspend fun getRandomQuestions(): List<QuizQuestionEntity>

    @Query("SELECT * FROM quiz_questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): QuizQuestionEntity?

    @Query("SELECT COUNT(*) FROM quiz_questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestionEntity>)

    @Query("DELETE FROM quiz_questions WHERE id = :id")
    suspend fun deleteQuestion(id: Long)

    @Query("SELECT DISTINCT category FROM quiz_questions ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    // Attempts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity): Long

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAttempts(limit: Int): Flow<List<QuizAttemptEntity>>

    // Question Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionLogs(logs: List<QuestionAttemptLogEntity>)

    @Query("SELECT * FROM question_attempt_logs WHERE attemptId = :attemptId")
    suspend fun getLogsForAttempt(attemptId: Long): List<QuestionAttemptLogEntity>

    @Query("SELECT * FROM question_attempt_logs ORDER BY timestamp DESC")
    fun getAllQuestionLogs(): Flow<List<QuestionAttemptLogEntity>>

    @Query("SELECT * FROM question_attempt_logs WHERE isCorrect = 0 ORDER BY timestamp DESC LIMIT 50")
    fun getRecentMissedQuestions(): Flow<List<QuestionAttemptLogEntity>>
}
