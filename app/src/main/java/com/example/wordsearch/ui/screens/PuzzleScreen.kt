package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson

// PuzzleScreen to display puzzles for the selected level
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    level: Int,
    navigateToGameScreen: (Int, Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Level $level Puzzles") })
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            PuzzleSelectionScreen(level = level, navigateToGameScreen)
        }
    }
}

@Composable
fun PuzzleSelectionScreen(
    level: Int,
    navigateToGameScreen: (Int, Int) -> Unit,
) {
    // Assuming you have a function to get the correct file name based on level
    val fileName = "level_$level.json"
    val context = LocalContext.current

    var puzzles by remember { mutableStateOf<List<Puzzle>>(emptyList()) }

    LaunchedEffect(fileName) {
        puzzles = loadPuzzlesFromJson(context, fileName)
        println("puzzles: $puzzles")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Select a Puzzle", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        PuzzleGrid(
            puzzles = puzzles,
            onPuzzleClick = { puzzle ->
                // Handle puzzle selection here
                navigateToGameScreen(level, puzzle.id)
            },
        )
    }
}

@Composable
fun PuzzleGrid(
    puzzles: List<Puzzle>,
    onPuzzleClick: (Puzzle) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // 3 puzzles per row
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        items(puzzles) { puzzle ->
            PuzzleCircle(puzzle, onPuzzleClick)
        }
    }
}

@Composable
fun PuzzleCircle(
    puzzle: Puzzle,
    onPuzzleClick: (Puzzle) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(80.dp)
                .padding(8.dp)
                .background(Color.LightGray, shape = CircleShape)
                .clickable { onPuzzleClick(puzzle) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = puzzle.id.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
