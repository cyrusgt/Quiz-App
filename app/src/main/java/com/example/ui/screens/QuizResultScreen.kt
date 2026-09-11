package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CrimsonIncorrect
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import com.example.viewmodel.QuizViewModel

@Composable
fun QuizResultScreen(
    viewModel: QuizViewModel,
    onPlayAgain: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizState by viewModel.quizState.collectAsState()

    val total = quizState.questions.size
    val correctCount = quizState.sessionLogs.count { it.isCorrect }
    val accuracyPercent = if (total > 0) ((correctCount.toDouble() / total) * 100).toInt() else 0

    val (titleMessage, subtitleMessage) = when {
        accuracyPercent >= 90 -> Pair("Masterful Performance!", "Exceptional knowledge across this domain.")
        accuracyPercent >= 70 -> Pair("Impressive Work!", "Solid grasp of the subject material.")
        accuracyPercent >= 50 -> Pair("Good Effort!", "A few tricky questions to review and strengthen.")
        else -> Pair("Room to Grow!", "Practice these questions in the Review Dashboard.")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Celebration Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .testTag("result_celebration_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(IndigoPrimary, VioletSecondary))
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(44.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = titleMessage,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = subtitleMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Score Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${quizState.realTimeScore} PTS",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndigoPrimary,
                                    modifier = Modifier.testTag("final_score_text")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Summary Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultMetric(label = "Accuracy", value = "$accuracyPercent%")
                            ResultMetric(label = "Score", value = "$correctCount / $total")
                            ResultMetric(label = "Max Streak", value = "${quizState.maxStreak}x")
                            ResultMetric(label = "Time", value = "${quizState.totalTimeTakenSeconds}s")
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("play_again_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play Again", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = onNavigateToDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("view_dashboard_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Performance Trends & Insights", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Question By Question Detailed Review
        item {
            Text(
                text = "Session Answer Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        itemsIndexed(quizState.sessionLogs) { index, log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("log_item_$index"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (log.isCorrect) EmeraldCorrect.copy(alpha = 0.15f)
                                else CrimsonIncorrect.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (log.isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (log.isCorrect) EmeraldCorrect else CrimsonIncorrect,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${index + 1}. ${log.questionText}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (!log.isCorrect) {
                            Text(
                                text = "Your answer: ${log.selectedAnswer}",
                                fontSize = 12.sp,
                                color = CrimsonIncorrect,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "Correct answer: ${log.correctAnswer}",
                            fontSize = 12.sp,
                            color = EmeraldCorrect,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
