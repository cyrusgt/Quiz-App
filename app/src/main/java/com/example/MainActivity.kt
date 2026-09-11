package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CreatorScreen
import com.example.ui.screens.PlayScreen
import com.example.ui.screens.QuizActiveScreen
import com.example.ui.screens.QuizResultScreen
import com.example.ui.screens.ReviewDashboardScreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QuizViewModel

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    PLAY("Play", Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow, "nav_tab_play"),
    CREATOR("Creator", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline, "nav_tab_creator"),
    DASHBOARD("Review", Icons.Filled.Analytics, Icons.Outlined.Analytics, "nav_tab_dashboard")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: QuizViewModel = viewModel()
                QuizApp(viewModel)
            }
        }
    }
}

@Composable
fun QuizApp(viewModel: QuizViewModel) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.PLAY) }
    val quizState by viewModel.quizState.collectAsState()

    // When quiz is active, display full-screen quiz view without bottom navigation
    if (quizState.isActive) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            if (quizState.isQuizFinished) {
                QuizResultScreen(
                    viewModel = viewModel,
                    onPlayAgain = {
                        val cat = quizState.categoryName
                        viewModel.startQuiz(cat, quizState.questions.size)
                    },
                    onNavigateToDashboard = {
                        viewModel.exitQuiz()
                        selectedTab = MainTab.DASHBOARD
                    },
                    onHome = {
                        viewModel.exitQuiz()
                        selectedTab = MainTab.PLAY
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                QuizActiveScreen(
                    viewModel = viewModel,
                    onExit = {
                        viewModel.exitQuiz()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    MainTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = IndigoPrimary
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    MainTab.PLAY -> PlayScreen(
                        viewModel = viewModel,
                        onNavigateToCreator = { selectedTab = MainTab.CREATOR },
                        onNavigateToDashboard = { selectedTab = MainTab.DASHBOARD },
                        modifier = Modifier.padding(innerPadding)
                    )
                    MainTab.CREATOR -> CreatorScreen(
                        viewModel = viewModel,
                        onNavigateToPlay = { selectedTab = MainTab.PLAY },
                        modifier = Modifier.padding(innerPadding)
                    )
                    MainTab.DASHBOARD -> ReviewDashboardScreen(
                        viewModel = viewModel,
                        onStartTargetedPractice = { missedIds ->
                            viewModel.startTargetedPractice(missedIds)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
