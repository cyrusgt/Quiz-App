package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.QuestionAttemptLogEntity
import com.example.data.local.QuizDatabase
import com.example.data.local.QuizQuestionEntity
import com.example.data.repository.AreaForImprovement
import com.example.data.repository.CategoryMastery
import com.example.data.repository.MissedQuestionSummary
import com.example.data.repository.PerformanceTrendPoint
import com.example.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveQuizState(
    val isActive: Boolean = false,
    val categoryName: String = "All Categories",
    val questions: List<QuizQuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val currentShuffledOptions: List<String> = emptyList(),
    val selectedOption: String? = null,
    val isAnswerSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val realTimeScore: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val secondsRemaining: Int = 20,
    val timerMaxSeconds: Int = 20,
    val sessionLogs: List<QuestionAttemptLogEntity> = emptyList(),
    val isQuizFinished: Boolean = false,
    val totalTimeTakenSeconds: Int = 0
) {
    val currentQuestion: QuizQuestionEntity?
        get() = questions.getOrNull(currentIndex)
}

data class CreatorState(
    val category: String = "Science & Tech",
    val customCategoryInput: String = "",
    val questionText: String = "",
    val correctAnswer: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val difficulty: String = "Medium",
    val explanation: String = "",
    val isGeneratingOptions: Boolean = false,
    val optionsGeneratedSuccessfully: Boolean = false,
    val saveMessage: String? = null,
    val isSaved: Boolean = false
)

data class DashboardAnalyticsState(
    val totalQuizzes: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val overallAccuracy: Int = 0,
    val bestStreak: Int = 0,
    val trends: List<PerformanceTrendPoint> = emptyList(),
    val categoryMastery: List<CategoryMastery> = emptyList(),
    val areasForImprovement: List<AreaForImprovement> = emptyList(),
    val frequentlyMissed: List<MissedQuestionSummary> = emptyList(),
    val isLoading: Boolean = false
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    init {
        val database = QuizDatabase.getDatabase(application, viewModelScope)
        repository = QuizRepository(database.quizDao())
        viewModelScope.launch {
            repository.ensureDatabasePopulated()
            loadDashboardAnalytics()
        }
    }

    val allQuestions: StateFlow<List<QuizQuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<String>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Quiz State
    private val _quizState = MutableStateFlow(ActiveQuizState())
    val quizState: StateFlow<ActiveQuizState> = _quizState.asStateFlow()

    // Creator State
    private val _creatorState = MutableStateFlow(CreatorState())
    val creatorState: StateFlow<CreatorState> = _creatorState.asStateFlow()

    // Dashboard Analytics State
    private val _dashboardState = MutableStateFlow(DashboardAnalyticsState())
    val dashboardState: StateFlow<DashboardAnalyticsState> = _dashboardState.asStateFlow()

    private var timerJob: Job? = null
    private var quizStartTimeMillis: Long = 0L

    fun startQuiz(category: String = "All Categories", questionCount: Int = 10) {
        viewModelScope.launch {
            val questions = repository.getQuestionsForQuiz(category, questionCount)
            if (questions.isEmpty()) return@launch

            quizStartTimeMillis = System.currentTimeMillis()
            val firstQuestion = questions[0]
            val shuffled = firstQuestion.getShuffledOptions()

            _quizState.value = ActiveQuizState(
                isActive = true,
                categoryName = category,
                questions = questions,
                currentIndex = 0,
                currentShuffledOptions = shuffled,
                selectedOption = null,
                isAnswerSubmitted = false,
                isCorrect = false,
                realTimeScore = 0,
                currentStreak = 0,
                maxStreak = 0,
                secondsRemaining = 20,
                timerMaxSeconds = 20,
                sessionLogs = emptyList(),
                isQuizFinished = false
            )
            startTimer()
        }
    }

    fun startTargetedPractice(missedQuestionIds: List<Long>) {
        viewModelScope.launch {
            val questions = repository.getQuestionsByIds(missedQuestionIds)
            if (questions.isEmpty()) {
                startQuiz("All Categories", 5)
                return@launch
            }
            quizStartTimeMillis = System.currentTimeMillis()
            val firstQuestion = questions[0]
            val shuffled = firstQuestion.getShuffledOptions()

            _quizState.value = ActiveQuizState(
                isActive = true,
                categoryName = "Targeted Weak Areas",
                questions = questions,
                currentIndex = 0,
                currentShuffledOptions = shuffled,
                selectedOption = null,
                isAnswerSubmitted = false,
                isCorrect = false,
                realTimeScore = 0,
                currentStreak = 0,
                maxStreak = 0,
                secondsRemaining = 25,
                timerMaxSeconds = 25,
                sessionLogs = emptyList(),
                isQuizFinished = false
            )
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizState.value.secondsRemaining > 0 && !_quizState.value.isAnswerSubmitted && !_quizState.value.isQuizFinished) {
                delay(1000)
                _quizState.value = _quizState.value.copy(
                    secondsRemaining = _quizState.value.secondsRemaining - 1
                )
            }
            if (_quizState.value.secondsRemaining == 0 && !_quizState.value.isAnswerSubmitted && !_quizState.value.isQuizFinished) {
                // Time's up! Automatically submit as timeout
                submitAnswer(null)
            }
        }
    }

    fun submitAnswer(chosenOption: String?) {
        if (_quizState.value.isAnswerSubmitted) return
        timerJob?.cancel()

        val state = _quizState.value
        val currentQ = state.currentQuestion ?: return
        val isCorrect = chosenOption != null && chosenOption.equals(currentQ.correctAnswer, ignoreCase = true)

        // Real-time score calculation
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newMaxStreak = maxOf(state.maxStreak, newStreak)

        // Base points + streak multiplier + speed bonus
        val basePoints = if (isCorrect) 100 else 0
        val streakBonus = if (isCorrect) (newStreak - 1) * 25 else 0
        val speedBonus = if (isCorrect) (state.secondsRemaining * 5) else 0
        val pointsEarned = basePoints + streakBonus + speedBonus
        val updatedScore = state.realTimeScore + pointsEarned

        val log = QuestionAttemptLogEntity(
            attemptId = 0,
            questionId = currentQ.id,
            questionText = currentQ.question,
            category = currentQ.category,
            selectedAnswer = chosenOption ?: "Timed Out",
            correctAnswer = currentQ.correctAnswer,
            isCorrect = isCorrect
        )

        _quizState.value = state.copy(
            selectedOption = chosenOption,
            isAnswerSubmitted = true,
            isCorrect = isCorrect,
            realTimeScore = updatedScore,
            currentStreak = newStreak,
            maxStreak = newMaxStreak,
            sessionLogs = state.sessionLogs + log
        )
    }

    fun nextQuestion() {
        val state = _quizState.value
        val nextIdx = state.currentIndex + 1

        if (nextIdx < state.questions.size) {
            val nextQ = state.questions[nextIdx]
            val shuffled = nextQ.getShuffledOptions()

            _quizState.value = state.copy(
                currentIndex = nextIdx,
                currentShuffledOptions = shuffled,
                selectedOption = null,
                isAnswerSubmitted = false,
                isCorrect = false,
                secondsRemaining = state.timerMaxSeconds
            )
            startTimer()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        val state = _quizState.value
        val timeTaken = ((System.currentTimeMillis() - quizStartTimeMillis) / 1000).toInt()
        val correctCount = state.sessionLogs.count { it.isCorrect }

        _quizState.value = state.copy(
            isQuizFinished = true,
            totalTimeTakenSeconds = timeTaken
        )

        viewModelScope.launch {
            repository.recordQuizSession(
                category = state.categoryName,
                totalQuestions = state.questions.size,
                correctCount = correctCount,
                score = state.realTimeScore,
                maxStreak = state.maxStreak,
                timeTakenSeconds = timeTaken,
                logs = state.sessionLogs
            )
            loadDashboardAnalytics()
        }
    }

    fun exitQuiz() {
        timerJob?.cancel()
        _quizState.value = ActiveQuizState()
    }

    // Creator Section Methods
    fun updateCreatorCategory(category: String) {
        _creatorState.value = _creatorState.value.copy(category = category)
    }

    fun updateCreatorCustomCategory(category: String) {
        _creatorState.value = _creatorState.value.copy(customCategoryInput = category)
    }

    fun updateCreatorQuestion(text: String) {
        _creatorState.value = _creatorState.value.copy(questionText = text, saveMessage = null)
    }

    fun updateCreatorAnswer(text: String) {
        _creatorState.value = _creatorState.value.copy(correctAnswer = text, saveMessage = null)
    }

    fun updateCreatorDifficulty(difficulty: String) {
        _creatorState.value = _creatorState.value.copy(difficulty = difficulty)
    }

    fun updateCreatorExplanation(explanation: String) {
        _creatorState.value = _creatorState.value.copy(explanation = explanation)
    }

    fun updateCreatorOption(index: Int, text: String) {
        val current = _creatorState.value
        _creatorState.value = when (index) {
            1 -> current.copy(option1 = text)
            2 -> current.copy(option2 = text)
            3 -> current.copy(option3 = text)
            else -> current
        }
    }

    fun autoGenerateOptions() {
        val state = _creatorState.value
        if (state.questionText.isBlank() || state.correctAnswer.isBlank()) {
            _creatorState.value = state.copy(
                saveMessage = "Please enter both the Question and the Correct Answer first."
            )
            return
        }

        val effectiveCategory = if (state.category == "Custom") {
            if (state.customCategoryInput.isNotBlank()) state.customCategoryInput else "General Knowledge"
        } else {
            state.category
        }

        _creatorState.value = state.copy(isGeneratingOptions = true, saveMessage = null)

        viewModelScope.launch {
            val generated = repository.generateOptionsForQuestion(
                question = state.questionText,
                correctAnswer = state.correctAnswer,
                category = effectiveCategory
            )

            val opt1 = generated.getOrElse(0) { "Option A" }
            val opt2 = generated.getOrElse(1) { "Option B" }
            val opt3 = generated.getOrElse(2) { "Option C" }

            _creatorState.value = _creatorState.value.copy(
                option1 = opt1,
                option2 = opt2,
                option3 = opt3,
                isGeneratingOptions = false,
                optionsGeneratedSuccessfully = true,
                saveMessage = "3 plausible options automatically generated!"
            )
        }
    }

    fun saveCreatedQuestion() {
        val state = _creatorState.value
        if (state.questionText.isBlank()) {
            _creatorState.value = state.copy(saveMessage = "Question cannot be empty.")
            return
        }
        if (state.correctAnswer.isBlank()) {
            _creatorState.value = state.copy(saveMessage = "Correct answer cannot be empty.")
            return
        }
        if (state.option1.isBlank() || state.option2.isBlank() || state.option3.isBlank()) {
            _creatorState.value = state.copy(saveMessage = "Please generate or fill all 3 distractor options.")
            return
        }

        val effectiveCategory = if (state.category == "Custom") {
            if (state.customCategoryInput.isNotBlank()) state.customCategoryInput.trim() else "Custom Quiz"
        } else {
            state.category
        }

        viewModelScope.launch {
            repository.saveCustomQuestion(
                category = effectiveCategory,
                question = state.questionText,
                correctAnswer = state.correctAnswer,
                option1 = state.option1,
                option2 = state.option2,
                option3 = state.option3,
                difficulty = state.difficulty,
                explanation = state.explanation
            )

            _creatorState.value = CreatorState(
                category = effectiveCategory,
                isSaved = true,
                saveMessage = "Quiz Question saved successfully! It is now playable."
            )
            loadDashboardAnalytics()
        }
    }

    fun resetCreatorForm() {
        _creatorState.value = CreatorState()
    }

    // Dashboard analytics loading
    fun loadDashboardAnalytics() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isLoading = true)

            val trends = repository.getPerformanceTrends()
            val mastery = repository.getCategoryMasteryList()
            val areas = repository.getAreasForImprovement()
            val missed = repository.getFrequentlyMissedQuestions()

            val totalQuizzes = trends.size
            val totalQuestions = trends.sumOf { it.totalQuestions }
            val totalCorrect = trends.sumOf { (it.scorePercent * it.totalQuestions) / 100 }
            val overallAccuracy = if (totalQuestions > 0) ((totalCorrect.toDouble() / totalQuestions) * 100).toInt() else 0
            val bestStreak = trends.maxOfOrNull { it.score / 100 } ?: 0

            _dashboardState.value = DashboardAnalyticsState(
                totalQuizzes = totalQuizzes,
                totalQuestionsAnswered = totalQuestions,
                overallAccuracy = overallAccuracy,
                bestStreak = bestStreak,
                trends = trends,
                categoryMastery = mastery,
                areasForImprovement = areas,
                frequentlyMissed = missed,
                isLoading = false
            )
        }
    }
}
