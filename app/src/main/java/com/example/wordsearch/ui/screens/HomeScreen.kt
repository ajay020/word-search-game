package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.data.PuzzleProgress
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson
import com.example.wordsearch.utils.PuzzleUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WordSearch") })
        },
    ) {
        HomeScreenContent(
            modifier = Modifier.padding(it),
            navigateToGameScreen = { navController.navigate("game/$it") },
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navigateToGameScreen: (Int) -> Unit,
) {
    val context = LocalContext.current

    var puzzles by remember { mutableStateOf<List<Puzzle>>(emptyList()) }
    var puzzleProgressList by remember { mutableStateOf<List<PuzzleProgress>>(emptyList()) }

    LaunchedEffect(Unit) {
        puzzles = loadPuzzlesFromJson(context)
        puzzleProgressList = PuzzleUtils.getPuzzleProgress(puzzles)
        println("puzzles: $puzzles")
    }

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
                onPuzzleClick = navigateToGameScreen,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreenContent {

    }
}
