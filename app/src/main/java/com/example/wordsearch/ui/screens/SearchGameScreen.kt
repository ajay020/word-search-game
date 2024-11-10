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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
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
import com.example.wordsearch.R
import com.example.wordsearch.ui.components.CircularBtnBackground
import com.example.wordsearch.ui.components.SearchGrid
import com.example.wordsearch.ui.components.SearchGridTopbar
import com.example.wordsearch.ui.components.TimerExpiredDialog
import com.example.wordsearch.viewModels.SearchGameViewModal
import com.example.wordsearch.viewModels.SearchGridViewModel
import java.util.Locale

@Composable
fun SearchGameScreen(
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
//        Image(
//            painter = painterResource(id = uiState.backgroundImgRes),
//            contentDescription = "Background",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop,
//        )

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
                    remainingTime = remainingTime,
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
            MainContent(
                modifier = Modifier.padding(it),
                resetTimer = {
                    searchGameViewModel.resetTimer()
                },
                navigateToMainScreen = navigateToMainScreen,
                onHintClick = {
                    if (uiState.availableHints > 0) {
                        searchGameViewModel.useHint()
                        searchGridViewModel.highlightFirstCharacter()
                    }
                },
                onSettingsClick = { showSettingsDialog = true },
            )

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
fun MainContent(
    modifier: Modifier = Modifier,
    navigateToMainScreen: () -> Unit,
    resetTimer: () -> Unit,
    onHintClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
    ) {
        SearchGrid(
            modifier = Modifier,
            resetTimer = { resetTimer() },
            navigateToMainScreen = { navigateToMainScreen() },
        )
        Spacer(modifier = Modifier.height(14.dp))
        PuzzleControl(
            onHintClick = onHintClick,
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
fun PuzzleControl(
    modifier: Modifier = Modifier,
    onHintClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row (
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        CircularBtnBackground(){
            IconButton(onClick = onHintClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lightbulb),
                    contentDescription = "Light bulb",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
       CircularBtnBackground(){
           IconButton(onClick = { onSettingsClick() }) {
               Icon(
                   Icons.Default.Settings,
                   contentDescription = "Setting icon",
                   tint = Color.White,
                   modifier = Modifier.size(36.dp),
               )
           }
       }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun SearchGreenScreenPreview() {

   PuzzleControl(onHintClick = { /*TODO*/ }) {
       
    }
}
