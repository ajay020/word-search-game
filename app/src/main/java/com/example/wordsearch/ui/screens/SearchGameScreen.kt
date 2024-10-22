package com.example.wordsearch.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    }
}

@Composable
fun ExitConfirmationDialog(
    onQuit: () -> Unit,
    onContinue: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation( 8.dp)
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Want to quit? Your progress will be lost",
                    modifier =
                        Modifier
                            .padding(18.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                        onClick = {
                            onContinue()
                        },
                    ) {
                        Text("Continue")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        modifier =
                        Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onQuit()
                        },
                    ) {
                        Text("Quit")
                    }
                }
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
//    SearchGameScreen(navigateToMainScreen = {
//        navController.navigate("main")
//    })

    ExitConfirmationDialog(onQuit = { /*TODO*/ }) {}
}
