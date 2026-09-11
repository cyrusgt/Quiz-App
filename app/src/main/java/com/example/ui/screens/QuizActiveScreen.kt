package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonIncorrect
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.viewmodel.QuizViewModel

@Composable
fun QuizActiveScreen(
    viewModel: QuizViewModel,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentQuestion = quizState.currentQuestion
    if (currentQuestion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading quiz question...")
        }
        return
    }

    val totalQuestions = quizState.questions.size
    val currentNum = quizState.currentIndex + 1
    val progress = (currentNum.toFloat() / totalQuestions).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(scrollState)
    ) {
        // Top Navigation & HUD Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.exitQuiz()
                    onExit()
                },
                modifier = Modifier.testTag("exit_quiz_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Quiz",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Category & Question Counter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = quizState.categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Question $currentNum of $totalQuestions",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Real-time Score HUD
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = IndigoPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Scoreboard,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quizState.realTimeScore}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = IndigoPrimary,
                        modifier = Modifier.testTag("real_time_score_text")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Question Progress Bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = IndigoPrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Real-time Status Row: Timer & Streak Bonus
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Timer Chip
            val timerColor = when {
                quizState.secondsRemaining <= 5 -> CrimsonIncorrect
                quizState.secondsRemaining <= 10 -> AmberAccent
                else -> IndigoPrimary
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(timerColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = timerColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${quizState.secondsRemaining}s",
                    color = timerColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.testTag("timer_text")
                )
            }

            // Streak Indicator
            if (quizState.currentStreak > 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AmberAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quizState.currentStreak}x Streak (+${(quizState.currentStreak - 1) * 25} pts)",
                        color = AmberAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.testTag("streak_bonus_text")
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = currentQuestion.difficulty,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Prompt Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("question_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = currentQuestion.question,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("question_text")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4 MCQ Option Cards
        val optionLabels = listOf("A", "B", "C", "D")
        quizState.currentShuffledOptions.forEachIndexed { index, optionText ->
            val label = optionLabels.getOrElse(index) { "?" }
            val isSelected = quizState.selectedOption == optionText
            val isAnswerSubmitted = quizState.isAnswerSubmitted
            val isThisCorrect = optionText.equals(currentQuestion.correctAnswer, ignoreCase = true)

            // Determine card styling based on submitted state
            val containerColor by animateColorAsState(
                targetValue = when {
                    !isAnswerSubmitted && isSelected -> IndigoPrimary.copy(alpha = 0.15f)
                    isAnswerSubmitted && isThisCorrect -> EmeraldContainer
                    isAnswerSubmitted && isSelected && !isThisCorrect -> CrimsonContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "option_color"
            )

            val borderColor = when {
                !isAnswerSubmitted && isSelected -> IndigoPrimary
                isAnswerSubmitted && isThisCorrect -> EmeraldCorrect
                isAnswerSubmitted && isSelected && !isThisCorrect -> CrimsonIncorrect
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(16.dp))
                    .clickable(enabled = !isAnswerSubmitted) {
                        triggerVibration(context)
                        viewModel.submitAnswer(optionText)
                    }
                    .testTag("option_button_$index"),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge (A, B, C, D)
                    val badgeColor = when {
                        isAnswerSubmitted && isThisCorrect -> EmeraldCorrect
                        isAnswerSubmitted && isSelected && !isThisCorrect -> CrimsonIncorrect
                        !isAnswerSubmitted && isSelected -> IndigoPrimary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val badgeTextColor = when {
                        isAnswerSubmitted && (isThisCorrect || isSelected) -> Color.White
                        !isAnswerSubmitted && isSelected -> Color.White
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAnswerSubmitted && isThisCorrect) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (isAnswerSubmitted && isSelected && !isThisCorrect) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                color = badgeTextColor,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = optionText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected || (isAnswerSubmitted && isThisCorrect)) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = when {
                            isAnswerSubmitted && isThisCorrect -> EmeraldCorrect
                            isAnswerSubmitted && isSelected && !isThisCorrect -> CrimsonIncorrect
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Real-Time Explanation Card (revealed upon answer submission)
        AnimatedVisibility(
            visible = quizState.isAnswerSubmitted,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explanation_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (quizState.isCorrect) EmeraldContainer.copy(alpha = 0.5f)
                        else CrimsonContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = if (quizState.isCorrect) EmeraldCorrect else CrimsonIncorrect,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (quizState.isCorrect) "Spot on! +100 PTS" else "Incorrect!",
                                fontWeight = FontWeight.Bold,
                                color = if (quizState.isCorrect) EmeraldCorrect else CrimsonIncorrect,
                                fontSize = 14.sp
                            )
                            if (currentQuestion.explanation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Continue / Next Button
                Button(
                    onClick = { viewModel.nextQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_question_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text(
                        text = if (quizState.currentIndex + 1 < quizState.questions.size) "Next Question" else "View Results",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun triggerVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    } catch (e: Exception) {
        // Graceful vibration fallback
    }
}
