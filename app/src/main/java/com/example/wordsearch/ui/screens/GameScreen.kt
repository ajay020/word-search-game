package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.R
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.data.PuzzlePart
import com.example.wordsearch.ui.components.CongratsDialog
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson
import com.example.wordsearch.utils.GridUtils.generateGrid
import com.example.wordsearch.viewModels.GameViewModel
import com.example.wordsearch.viewModels.SearchGridViewModel

const val TAG = "GameScreen"

@Composable
fun GameScreen(
    puzzleId: Int,
    navigateToHomeScreen: () -> Unit,
    gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory),
) {
    val context = LocalContext.current
    val searchGridViewModel: SearchGridViewModel = viewModel()

    val grid by gameViewModel.grid.collectAsState()
    val isLoading by gameViewModel.isLoading
    val coins by gameViewModel.coins.collectAsState()
    var puzzle by remember { mutableStateOf<Puzzle?>(null) }
    var puzzleParts by remember { mutableStateOf<List<PuzzlePart>>(emptyList()) }
    val isGameCompleted = searchGridViewModel.isPuzzleCompleted
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
            gameViewModel.initGrid(it.words)
        }
    }

    Scaffold(
        topBar = {
            GameScreenTopBar(
                title = "Game screen",
                coins = coins,
                onCloseClick = { navigateToHomeScreen() },
                onHintClick = {
                    gameViewModel.useHint()
                    if (coins > 0) {
                        searchGridViewModel.highlightFirstCharacter()
                    }
                },
            )
        },
    ) { innerPadding ->

        if (isLoading) {
            Box(
                modifier =
                    Modifier
                        .background(Color.Blue.copy(alpha = 0.5f))
                        .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = Color.Blue,
                )
            }
        }
        if (!isLoading && puzzleParts.isNotEmpty() && currentPuzzlePartIndex < puzzleParts.size) {
            GameScreenContent(
                modifier = Modifier.padding(innerPadding),
                puzzlePart = puzzleParts[currentPuzzlePartIndex],
                grid = grid,
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
                searchGridViewModel.resetPuzzleState()
            },
        )
    }
}

@Composable
fun GameScreenContent(
    modifier: Modifier = Modifier,
    puzzlePart: PuzzlePart,
    grid: List<List<Char>>,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.LightGray),
        verticalArrangement = Arrangement.Top,
    ) {
//        if (grid.isNotEmpty()) {
//            SearchGrid(grid = grid, wordList = puzzlePart.words)
        //            WordGrid(
        //                wordList = puzzlePart.words,
        //                grid = grid,
        //            )
//        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreenTopBar(
    title: String,
    coins: Int,
    onCloseClick: () -> Unit,
    onHintClick: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.background(Color.White.copy(alpha = 1f)),
        colors =
            TopAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = Color.White,
                navigationIconContentColor = Color.Black,
                titleContentColor = Color.Black,
                scrolledContainerColor = Color.Black,
            ),
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Pause",
                )
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .background(Color.Yellow, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_monetization_on),
                    contentDescription = "Coins",
                    tint = Color.Blue,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = coins.toString(),
                    fontSize = 14.sp,
                    color = Color.Blue,
                )
            }
            IconButton(onClick = onHintClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lightbulb),
                    contentDescription = "Light bulb",
                    tint = Color.Red,
                )
            }
        },
    )
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun GameScreenPreview() {
    val words =
        listOf(
            "all",
            "you",
            "have",
            "is",
            "now",
        )
    val puzzlePart = PuzzlePart(1, words)

    GameScreenContent(
        puzzlePart = puzzlePart,
        grid = generateGrid(words),
    )
    //    ExitDialog(
    //        navigateToHomeScreen = {},
    //        onDismiss = {},
    //    )
    GameScreenTopBar(
        title = "Game screen",
        coins = 100,
        onCloseClick = {},
        onHintClick = {},
    )
}
