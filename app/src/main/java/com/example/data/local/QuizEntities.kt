package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val question: String,
    val correctAnswer: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val difficulty: String = "Medium",
    val explanation: String = "",
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getShuffledOptions(): List<String> {
        val list = mutableListOf(correctAnswer, option1, option2, option3)
        list.shuffle()
        return list
    }
}

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val score: Int,
    val maxStreak: Int,
    val timeTakenSeconds: Int
)

@Entity(tableName = "question_attempt_logs")
data class QuestionAttemptLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptId: Long,
    val questionId: Long,
    val questionText: String,
    val category: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
