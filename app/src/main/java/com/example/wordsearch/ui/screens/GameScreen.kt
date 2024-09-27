@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import android.util.Log
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.ui.components.CongratsDialog
import com.example.wordsearch.ui.components.WordGrid
import com.example.wordsearch.ui.viewModels.GameViewModel
import com.example.wordsearch.ui.viewModels.WordGridViewModel
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson

const val TAG = "WordSearchScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    level: Int,
    puzzleId: Int,
    navigateToHomeScreen: () -> Unit,
) {
    val fileName = "level_$level.json"
    val context = LocalContext.current
    val wordGridViewModal: WordGridViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()

    var puzzle by remember { mutableStateOf<Puzzle?>(null) }
    val isGameCompleted by wordGridViewModal.isGameCompleted.collectAsState()
    val currentPuzzleId by gameViewModel.currentPuzzleId.collectAsState()
    val isAllPuzzlesCompleted by gameViewModel.isAllPuzzlesCompleted.collectAsState()

    LaunchedEffect(fileName) {
        gameViewModel.initCurrentPuzzleId(puzzleId)
        val puzzles = loadPuzzlesFromJson(context, fileName)
        gameViewModel.initMaxPuzzlesPerLevel(puzzles.size)
    }

    LaunchedEffect(fileName, currentPuzzleId) {
        val puzzles = loadPuzzlesFromJson(context, fileName)
        puzzle = puzzles.firstOrNull { it.id == currentPuzzleId }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.Blue.copy(alpha = 0.3f)),
                title = { Text("Level $level") },
            )
        },
    ) { innerPadding ->
        val screenSize = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val screenLayout = LocalConfiguration.current.screenLayout

        Log.d("GameScreen", "screen width: $screenSize height: $screenHeight layout: $screenLayout")

        GameScreenContent(
            modifier = Modifier.padding(innerPadding),
            puzzle = puzzle,
        )
    }

    if (isAllPuzzlesCompleted) {
        ExitDialog(
            modifier = Modifier,
            navigateToHomeScreen,
            onDismiss = { gameViewModel.resetGameState() },
        )
    }

    if (isGameCompleted) {
        // Show congrats dialog
        CongratsDialog(
            onDismiss = { /* Maybe reset or dismiss */ },
            onNextGame = {
                wordGridViewModal.resetGameState()
                gameViewModel.onNextPuzzle()
            },
        )
    }
}

@Composable
fun GameScreenContent(
    modifier: Modifier = Modifier,
    puzzle: Puzzle?,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
    ) {
        puzzle?.let {
            WordGrid(puzzle = puzzle!!)
        }
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
        text = { Text("You've completed all the puzzles of this level.") },
        confirmButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navigateToHomeScreen()
                    onDismiss()
                },
            ) {
                Text("Go to next level")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    val puzzle =
        Puzzle(
            1,
            listOf("APPLE", "BANANA", "CHERRY", "DATE", "EGG", "FRUIT", "GRAPE"),
        )
    GameScreenContent(puzzle = puzzle)
//    ExitDialog(
//        navigateToHomeScreen = {},
//        onDismiss = {},
//    )
}
