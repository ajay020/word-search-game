package com.example.wordsearch.ui.screens

import ExitConfirmationDialog
import SettingsDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.components.SearchGrid
import com.example.wordsearch.ui.components.SearchGridTopbar
import com.example.wordsearch.viewModels.SearchGameViewModal
import com.example.wordsearch.viewModels.SearchGridViewModel

@Composable
fun SearchGameScreen(
    modifier: Modifier = Modifier,
    searchGameViewModal: SearchGameViewModal = viewModel(factory = SearchGameViewModal.FACTORY),
    searchGridViewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
    navigateToMainScreen: () -> Unit,
) {
    val uiState by searchGameViewModal.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        searchGameViewModal.startBackgroundMusic()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> searchGameViewModal.pauseMusic()
                    Lifecycle.Event.ON_RESUME -> searchGameViewModal.resumeMusic()
                    else -> {}
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

        Scaffold(
            modifier =
                Modifier
                    .background(Color.Transparent)
                    .fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            topBar = {
                SearchGridTopbar(
                    title = "Word Search",
                    coins = uiState.coins,
                    onCloseClick = { showDialog = true },
                    onHintClick = {
                        if (uiState.availableHints > 0) {
                            searchGameViewModal.useHint()
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
                SearchGrid(
                    modifier = Modifier,
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
                    onMusicToggle = { searchGameViewModal.toggleMusic() },
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
//    ExitConfirmationDialog(onQuit = { /*TODO*/ }) {}
}
