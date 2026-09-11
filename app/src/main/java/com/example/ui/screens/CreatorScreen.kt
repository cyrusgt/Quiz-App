package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldCorrect
import com.example.ui.theme.IndigoPrimary
import com.example.viewmodel.QuizViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatorScreen(
    viewModel: QuizViewModel,
    onNavigateToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val creatorState by viewModel.creatorState.collectAsState()
    val allQuestions by viewModel.allQuestions.collectAsState()
    val focusManager = LocalFocusManager.current

    val customQuestions = allQuestions.filter { it.isCustom }

    val categories = listOf(
        "Science & Tech",
        "History & Geography",
        "Arts & Literature",
        "Pop Culture & Cinema",
        "General Knowledge",
        "Custom"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Creator Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creator_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Quiz Creator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter a question & answer — the app auto-generates options for your MCQ!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Category Selector Chips
        item {
            Column {
                Text(
                    text = "1. Select Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = creatorState.category == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateCreatorCategory(cat) },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndigoPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_${cat.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                if (creatorState.category == "Custom") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = creatorState.customCategoryInput,
                        onValueChange = { viewModel.updateCreatorCustomCategory(it) },
                        label = { Text("Custom Category Name") },
                        placeholder = { Text("e.g. World Architecture") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_category_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Question Input
        item {
            Column {
                Text(
                    text = "2. Enter Question",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = creatorState.questionText,
                    onValueChange = { viewModel.updateCreatorQuestion(it) },
                    placeholder = { Text("e.g. What is the chemical symbol for Gold?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_question_input"),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2,
                    maxLines = 4
                )
            }
        }

        // Correct Answer Input
        item {
            Column {
                Text(
                    text = "3. Enter Correct Answer",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = creatorState.correctAnswer,
                    onValueChange = { viewModel.updateCreatorAnswer(it) },
                    placeholder = { Text("e.g. Au") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_answer_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        if (creatorState.questionText.isNotBlank() && creatorState.correctAnswer.isNotBlank()) {
                            viewModel.autoGenerateOptions()
                        }
                    }),
                    trailingIcon = {
                        if (creatorState.correctAnswer.isNotBlank()) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Valid Answer",
                                tint = EmeraldCorrect
                            )
                        }
                    }
                )
            }
        }

        // Automatic Option Generator Trigger Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Automatic MCQ Options",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (creatorState.optionsGeneratedSuccessfully) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldContainer
                            ) {
                                Text(
                                    text = "Generated",
                                    color = EmeraldCorrect,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Once you provide the question and correct answer, tap below to automatically generate 3 plausible multiple choice options.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.autoGenerateOptions()
                        },
                        enabled = !creatorState.isGeneratingOptions &&
                                creatorState.questionText.isNotBlank() &&
                                creatorState.correctAnswer.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auto_generate_options_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        if (creatorState.isGeneratingOptions) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating smart options...", fontSize = 14.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AmberAccent
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (creatorState.optionsGeneratedSuccessfully) "Regenerate Options" else "✨ Auto-Generate Options",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 4 MCQ Options Display & Editor
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "4. Review & Edit MCQ Options",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Option A: Correct Answer (Fixed/Highlighted)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.5.dp, EmeraldCorrect),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(EmeraldCorrect),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Correct Answer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldCorrect
                            )
                            Text(
                                text = creatorState.correctAnswer.ifBlank { "Awaiting correct answer above..." },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Generated Option 1
                OutlinedTextField(
                    value = creatorState.option1,
                    onValueChange = { viewModel.updateCreatorOption(1, it) },
                    label = { Text("Distractor Option 1") },
                    placeholder = { Text("Will auto-generate...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("option_1_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Generated Option 2
                OutlinedTextField(
                    value = creatorState.option2,
                    onValueChange = { viewModel.updateCreatorOption(2, it) },
                    label = { Text("Distractor Option 2") },
                    placeholder = { Text("Will auto-generate...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("option_2_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Generated Option 3
                OutlinedTextField(
                    value = creatorState.option3,
                    onValueChange = { viewModel.updateCreatorOption(3, it) },
                    label = { Text("Distractor Option 3") },
                    placeholder = { Text("Will auto-generate...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("option_3_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        }

        // Difficulty Selector & Explanation
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "5. Additional Details (Optional)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Easy", "Medium", "Hard").forEach { diff ->
                        FilterChip(
                            selected = creatorState.difficulty == diff,
                            onClick = { viewModel.updateCreatorDifficulty(diff) },
                            label = { Text(diff, fontSize = 12.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = creatorState.explanation,
                    onValueChange = { viewModel.updateCreatorExplanation(it) },
                    label = { Text("Explanation / Did You Know?") },
                    placeholder = { Text("Why this answer is correct (shown in quiz review)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_explanation_input"),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
            }
        }

        // Save Status Message Banner
        if (creatorState.saveMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (creatorState.isSaved) EmeraldContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (creatorState.isSaved) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (creatorState.isSaved) EmeraldCorrect else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = creatorState.saveMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (creatorState.isSaved) EmeraldCorrect else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Save Question Action Button
        item {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveCreatedQuestion()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_question_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Question to Quiz", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // List of Custom Questions Created So Far
        if (customQuestions.isNotEmpty()) {
            item {
                Text(
                    text = "Your Created Questions (${customQuestions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            customQuestions.forEach { question ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = IndigoPrimary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = question.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = question.difficulty,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = question.question,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Answer: ${question.correctAnswer}",
                                fontSize = 12.sp,
                                color = EmeraldCorrect,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
