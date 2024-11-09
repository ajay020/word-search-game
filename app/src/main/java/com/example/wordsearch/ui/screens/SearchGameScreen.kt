package com.example.wordsearch.ui.screens

import ExitConfirmationDialog
import SettingsDialog
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.components.SearchGrid
import com.example.wordsearch.ui.components.SearchGridTopbar
import com.example.wordsearch.ui.components.TimerExpiredDialog
import com.example.wordsearch.viewModels.SearchGameViewModal
import com.example.wordsearch.viewModels.SearchGridViewModel
import java.util.Locale

@Composable
fun SearchGameScreen(
    modifier: Modifier = Modifier,
    searchGameViewModel: SearchGameViewModal = viewModel(factory = SearchGameViewModal.FACTORY),
    searchGridViewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
    navigateToMainScreen: () -> Unit,
) {
    val uiState by searchGameViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember {
        mutableStateOf(false)
    }
    val wordTheme = searchGridViewModel.theme.collectAsState()

    val remainingTime by searchGameViewModel.remainingTime.collectAsState()
    val isTimerExpired by searchGameViewModel.isTimerExpired.collectAsState()
    var showTimerExpiredDialog by remember { mutableStateOf(false) }
    if (isTimerExpired) showTimerExpiredDialog = true

    val hasGameStarted =
        searchGridViewModel.uiState.value.foundWords
            .isNotEmpty()

    // pause timer when puzzle completes
    if (searchGridViewModel.uiState.value.showCompletionDialog) {
        searchGameViewModel.pauseTimer()
    }

    LaunchedEffect(Unit) {
        searchGameViewModel.startBackgroundMusic()
        searchGameViewModel.startTimer()
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        searchGameViewModel.pauseMusic()
                        searchGameViewModel.pauseTimer()
                        Log.d("SearchGame", "on pause")
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        searchGameViewModel.resumeMusic()
                        searchGameViewModel.resumeTimer()
                        Log.d("SearchGame", "on resume")
                    }

                    else -> {
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = uiState.backgroundImgRes),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Handle back press
        BackHandler(
            enabled = hasGameStarted,
            onBack = {
                showDialog = true
            },
        )

        Scaffold(
            modifier =
                Modifier
                    .background(Color.Transparent)
                    .fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            topBar = {
                SearchGridTopbar(
                    title = wordTheme.value ?: "Word Search",
                    coins = uiState.coins,
                    onCloseClick = {
                        if (hasGameStarted) {
                            showDialog = true
                        } else {
                            navigateToMainScreen()
                        }
                    },
                    onHintClick = {
                        if (uiState.availableHints > 0) {
                            searchGameViewModel.useHint()
                            searchGridViewModel.highlightFirstCharacter()
                        }
                    },
                    onSettingsClick = { showSettingsDialog = true },
                )
            },
        ) {
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(it),
                verticalArrangement = Arrangement.Center,
            ) {
                // Display timer
                TimerText(remainingTime = remainingTime)
                // Display the search grid
                SearchGrid(
                    modifier = Modifier,
                    resetTimer = { searchGameViewModel.resetTimer() },
                    navigateToMainScreen = { navigateToMainScreen() },
                )
            }

            if (showTimerExpiredDialog) {
                TimerExpiredDialog(
                    onRestartGame = {
                        showTimerExpiredDialog = false
                        searchGameViewModel.resetTimer()
                        searchGridViewModel.resetPuzzleState()
                    },
                    onExtendTime = {
                        showTimerExpiredDialog = false
                        searchGameViewModel.addExtraTime()
                    },
                )
            }

            // Show the custom dialog when showDialog is true
            if (showDialog) {
                ExitConfirmationDialog(
                    onContinue = { showDialog = false },
                    onQuit = {
                        showDialog = false
                        navigateToMainScreen()
                    }, // Navigate to main screen on quit
                )
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    onDismiss = { showSettingsDialog = false },
                    isSoundEnabled = searchGridViewModel.uiState.value.isSoundEnabled,
                    onSoundToggle = { searchGridViewModel.toggleSound() },
                    isMusicEnabled = uiState.musicEnabled,
                    onMusicToggle = { searchGameViewModel.toggleMusic() },
                )
            }
        }
    }
}

@Composable
fun TimerText(
    modifier: Modifier = Modifier,
    remainingTime: Int,
) {
    Row(
        modifier =
            Modifier
                .padding(8.dp)
                .background(Color.Gray.copy(alpha = 0.5f))
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = modifier.padding(8.dp),
            text = "Time: " + formatTime(remainingTime),
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
        )
    }
}

// Helper function to format time
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun SearchGreenScreenPreview() {
//    ExitConfirmationDialog(onQuit = { /*TODO*/ }) {}

    TimerText(remainingTime = 120)
}
