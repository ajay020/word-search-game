@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.data.PuzzlePart
import com.example.wordsearch.ui.components.CongratsDialog
import com.example.wordsearch.ui.components.WordGrid
import com.example.wordsearch.ui.viewModels.GameViewModel
import com.example.wordsearch.ui.viewModels.WordGridViewModel
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson

const val TAG = "WordSearchScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    puzzleId: Int,
    navigateToHomeScreen: () -> Unit,
    gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory),
) {
    val context = LocalContext.current
    val wordGridViewModal: WordGridViewModel = viewModel()

    var puzzle by remember { mutableStateOf<Puzzle?>(null) }
    var puzzleParts by remember { mutableStateOf<List<PuzzlePart>>(emptyList()) }
    val isGameCompleted by wordGridViewModal.isGameCompleted.collectAsState()
    val currentPuzzlePartIndex by gameViewModel.currentPuzzlePartIndex.collectAsState()
    var showExitDialog by remember { mutableStateOf(true) }

    LaunchedEffect(puzzleId) {
        puzzle = loadPuzzlesFromJson(context).firstOrNull { it.id == puzzleId }
        puzzle?.let {
            puzzleParts = it.parts
            gameViewModel.initCurrentPuzzlePartIndex(puzzleId)
        }
    }

    LaunchedEffect(currentPuzzlePartIndex) {
        puzzleParts.getOrNull(currentPuzzlePartIndex)?.let {
            wordGridViewModal.initGrid(it.words)
        }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Blue.copy(alpha = 0.3f)),
                title = { Text("Game Screen") },
            )
        },
    ) { innerPadding ->

        if (puzzleParts.isNotEmpty() && currentPuzzlePartIndex < puzzleParts.size) {
            GameScreenContent(
                modifier = Modifier.padding(innerPadding),
                puzzlePart = puzzleParts[currentPuzzlePartIndex],
            )
        }
    }

    // Show exit dialog when the game is completed and it's the last puzzle part
    if (isGameCompleted && currentPuzzlePartIndex >= puzzleParts.size - 1 && showExitDialog) {
        // Save progress
        gameViewModel.savePuzzleProgress(
            puzzleId = puzzleId,
            totalParts = puzzleParts.size,
            completedParts = currentPuzzlePartIndex + 1,
        )

        ExitDialog(
            modifier = Modifier,
            navigateToHomeScreen = {
                showExitDialog = false // Close the dialog before navigation
                navigateToHomeScreen()
            },
            onDismiss = {
                showExitDialog = false // Close the dialog on dismiss
            },
        )
    }

    // Show congrats dialog if the game is completed but it's not the last puzzle part
    if (isGameCompleted && currentPuzzlePartIndex < puzzleParts.size - 1) {
        // Save progress
        gameViewModel.savePuzzleProgress(
            puzzleId = puzzleId,
            completedParts = currentPuzzlePartIndex + 1,
            totalParts = puzzleParts.size,
        )

        CongratsDialog(
            onDismiss = { navigateToHomeScreen() },
            onNextGame = {
                gameViewModel.onNextPuzzlePart()
                // Reset the game state
                wordGridViewModal.resetGameState()
            },
        )
    }
}

@Composable
fun GameScreenContent(
    modifier: Modifier = Modifier,
    puzzlePart: PuzzlePart,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
    ) {
        WordGrid(wordList = puzzlePart.words)
    }
}

@Composable
fun ExitDialog(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { },
        title = { Text(text = "Great job!") },
        text = { Text("You have completed all puzzle in this stage!") },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onDismiss()
                    navigateToHomeScreen()
                },
            ) {
                Text("Go to next Stage")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    val puzzlePart = PuzzlePart(1, listOf("apple", "banana", "cherry"))
    GameScreenContent(puzzlePart = puzzlePart)
//    ExitDialog(
//        navigateToHomeScreen = {},
//        onDismiss = {},
//    )
}
