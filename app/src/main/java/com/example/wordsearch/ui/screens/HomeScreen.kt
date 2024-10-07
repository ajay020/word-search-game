package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.data.PuzzleProgress
import com.example.wordsearch.ui.viewModels.HomeViewModel
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val context = LocalContext.current

    var puzzles by remember { mutableStateOf<List<Puzzle>>(emptyList()) }
    var puzzleProgressList by remember { mutableStateOf<List<PuzzleProgress>>(emptyList()) }

    LaunchedEffect(Unit) {
        puzzles = loadPuzzlesFromJson(context)
        puzzles.forEach { puzzle ->
            val puzzleProgress = homeViewModel.getPuzzleProgress(puzzle.id)

            if (puzzleProgress != null) {
                puzzleProgressList = puzzleProgressList + puzzleProgress
            } else {
                puzzleProgressList =
                    puzzleProgressList + PuzzleProgress(puzzle.id, 0, puzzle.parts.size)
            }
        }
        println("puzzles: $puzzleProgressList")
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WordSearch") })
        },
    ) {
        HomeScreenContent(
            modifier = Modifier.padding(it),
            navigateToGameScreen = { navController.navigate("game/$it") },
            puzzleProgressList = puzzleProgressList,
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navigateToGameScreen: (Int) -> Unit,
    puzzleProgressList: List<PuzzleProgress>,
) {
    var showDialog by remember { mutableStateOf(false) }
    var lockedPuzzleMessage by remember { mutableStateOf("") }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PuzzleSelectionScreen(
                puzzleProgressList = puzzleProgressList,
                onPuzzleClick = { puzzleId, isLocked ->
                    if (!isLocked) {
                        navigateToGameScreen(puzzleId)
                    } else {
                        showDialog = true
                        lockedPuzzleMessage =
                            "Puzzle $puzzleId is locked. Complete the previous puzzle to unlock"
                    }
                },
            )
        }
    }
    if (showDialog) {
        LockedPuzzleDialog(
            message = lockedPuzzleMessage,
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
fun LockedPuzzleDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Puzzle Locked") },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
            ) {
                Text("OK")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {

}
