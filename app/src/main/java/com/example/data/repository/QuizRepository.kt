package com.example.data.repository

import com.example.data.local.PrepopulatedData
import com.example.data.local.QuestionAttemptLogEntity
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.QuizDao
import com.example.data.local.QuizQuestionEntity
import com.example.data.remote.DistractorGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PerformanceTrendPoint(
    val attemptId: Long,
    val dateLabel: String,
    val scorePercent: Int,
    val score: Int,
    val totalQuestions: Int,
    val category: String
)

data class CategoryMastery(
    val category: String,
    val totalAnswered: Int,
    val correctCount: Int,
    val accuracyPercent: Int
)

data class AreaForImprovement(
    val category: String,
    val accuracyPercent: Int,
    val recommendation: String,
    val missedCount: Int
)

data class MissedQuestionSummary(
    val questionId: Long,
    val questionText: String,
    val category: String,
    val correctAnswer: String,
    val lastWrongAnswer: String,
    val missCount: Int
)

class QuizRepository(private val quizDao: QuizDao) {

    val allQuestions: Flow<List<QuizQuestionEntity>> = quizDao.getAllQuestions()
    val allCategories: Flow<List<String>> = quizDao.getAllCategories()
    val allAttempts: Flow<List<QuizAttemptEntity>> = quizDao.getAllAttempts()
    val allLogs: Flow<List<QuestionAttemptLogEntity>> = quizDao.getAllQuestionLogs()
    val recentMissedLogs: Flow<List<QuestionAttemptLogEntity>> = quizDao.getRecentMissedQuestions()

    suspend fun ensureDatabasePopulated() {
        if (quizDao.getQuestionCount() == 0) {
            quizDao.insertQuestions(PrepopulatedData.initialQuestions)
        }
    }

    suspend fun getQuestionsForQuiz(category: String?, count: Int = 10): List<QuizQuestionEntity> {
        ensureDatabasePopulated()
        val questions = if (category.isNullOrBlank() || category == "All Categories") {
            quizDao.getRandomQuestions()
        } else {
            quizDao.getQuestionsByCategory(category)
        }
        return questions.take(count)
    }

    suspend fun getQuestionsByIds(ids: List<Long>): List<QuizQuestionEntity> {
        val result = mutableListOf<QuizQuestionEntity>()
        for (id in ids) {
            quizDao.getQuestionById(id)?.let { result.add(it) }
        }
        return result
    }

    suspend fun generateOptionsForQuestion(
        question: String,
        correctAnswer: String,
        category: String
    ): List<String> {
        return DistractorGenerator.generateDistractors(question, correctAnswer, category)
    }

    suspend fun saveCustomQuestion(
        category: String,
        question: String,
        correctAnswer: String,
        option1: String,
        option2: String,
        option3: String,
        difficulty: String = "Medium",
        explanation: String = ""
    ): Long {
        val entity = QuizQuestionEntity(
            category = category.trim(),
            question = question.trim(),
            correctAnswer = correctAnswer.trim(),
            option1 = option1.trim(),
            option2 = option2.trim(),
            option3 = option3.trim(),
            difficulty = difficulty,
            explanation = explanation.trim(),
            isCustom = true
        )
        return quizDao.insertQuestion(entity)
    }

    suspend fun recordQuizSession(
        category: String,
        totalQuestions: Int,
        correctCount: Int,
        score: Int,
        maxStreak: Int,
        timeTakenSeconds: Int,
        logs: List<QuestionAttemptLogEntity>
    ): Long {
        val attempt = QuizAttemptEntity(
            category = category,
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            score = score,
            maxStreak = maxStreak,
            timeTakenSeconds = timeTakenSeconds
        )
        val attemptId = quizDao.insertAttempt(attempt)
        val updatedLogs = logs.map { it.copy(attemptId = attemptId) }
        quizDao.insertQuestionLogs(updatedLogs)
        return attemptId
    }

    suspend fun deleteQuestion(id: Long) {
        quizDao.deleteQuestion(id)
    }

    // Analytics computation
    suspend fun getPerformanceTrends(): List<PerformanceTrendPoint> {
        val attempts = quizDao.getAllAttempts().first()
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        return attempts.reversed().map { attempt ->
            val pct = if (attempt.totalQuestions > 0) {
                ((attempt.correctCount.toDouble() / attempt.totalQuestions) * 100).toInt()
            } else 0
            PerformanceTrendPoint(
                attemptId = attempt.id,
                dateLabel = dateFormat.format(Date(attempt.timestamp)),
                scorePercent = pct,
                score = attempt.score,
                totalQuestions = attempt.totalQuestions,
                category = attempt.category
            )
        }
    }

    suspend fun getCategoryMasteryList(): List<CategoryMastery> {
        val logs = quizDao.getAllQuestionLogs().first()
        if (logs.isEmpty()) return emptyList()

        val grouped = logs.groupBy { it.category }
        return grouped.map { (category, categoryLogs) ->
            val total = categoryLogs.size
            val correct = categoryLogs.count { it.isCorrect }
            val accuracy = if (total > 0) ((correct.toDouble() / total) * 100).toInt() else 0
            CategoryMastery(
                category = category,
                totalAnswered = total,
                correctCount = correct,
                accuracyPercent = accuracy
            )
        }.sortedBy { it.accuracyPercent } // Lowest accuracy first to highlight areas needing attention
    }

    suspend fun getAreasForImprovement(): List<AreaForImprovement> {
        val mastery = getCategoryMasteryList()
        val logs = quizDao.getAllQuestionLogs().first()
        val missedLogs = logs.filter { !it.isCorrect }

        val areas = mutableListOf<AreaForImprovement>()

        // 1. Identify categories with accuracy < 75%
        for (cat in mastery) {
            val catMissed = missedLogs.count { it.category == cat.category }
            if (cat.accuracyPercent < 75 || catMissed >= 2) {
                val advice = when {
                    cat.accuracyPercent < 40 -> "Critical focus needed: Review foundational concepts in ${cat.category}."
                    cat.accuracyPercent < 65 -> "Moderate retention: Target practice in ${cat.category} to boost mastery above 70%."
                    else -> "Close to mastery! Drill missed questions in ${cat.category} to achieve a perfect streak."
                }
                areas.add(
                    AreaForImprovement(
                        category = cat.category,
                        accuracyPercent = cat.accuracyPercent,
                        recommendation = advice,
                        missedCount = catMissed
                    )
                )
            }
        }

        return areas
    }

    suspend fun getFrequentlyMissedQuestions(): List<MissedQuestionSummary> {
        val logs = quizDao.getAllQuestionLogs().first()
        val missed = logs.filter { !it.isCorrect }

        val grouped = missed.groupBy { it.questionId }
        return grouped.map { (questionId, questionLogs) ->
            val latest = questionLogs.maxByOrNull { it.timestamp }!!
            MissedQuestionSummary(
                questionId = questionId,
                questionText = latest.questionText,
                category = latest.category,
                correctAnswer = latest.correctAnswer,
                lastWrongAnswer = latest.selectedAnswer,
                missCount = questionLogs.size
            )
        }.sortedByDescending { it.missCount }
    }
}
