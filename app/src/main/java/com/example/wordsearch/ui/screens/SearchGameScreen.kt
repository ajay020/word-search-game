package com.example.wordsearch.ui.screens

import ExitConfirmationDialog
import SettingsDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.components.SearchGrid
import com.example.wordsearch.ui.components.SearchGridTopbar
import com.example.wordsearch.viewModels.SearchGameViewModal
import com.example.wordsearch.viewModels.SearchGridViewModel

@Composable
fun SearchGameScreen(
    modifier: Modifier = Modifier,
    searchGameViewModal: SearchGameViewModal = viewModel(),
    navigateToMainScreen: () -> Unit,
) {
    val searchGridViewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory)
    val uiState by searchGameViewModal.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
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
        SearchGrid(
            modifier = modifier.padding(it),
        )

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
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun SearchGreenScreenPreview() {
//    SearchGameScreen(navigateToMainScreen = {
//        navController.navigate("main")
//    })

    ExitConfirmationDialog(onQuit = { /*TODO*/ }) {}
}
