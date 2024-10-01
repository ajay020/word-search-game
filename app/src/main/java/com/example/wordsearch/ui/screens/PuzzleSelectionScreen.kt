package com.example.wordsearch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordsearch.data.PuzzleProgress

// PuzzleScreen to display puzzles for the selected level
@Composable
fun PuzzleSelectionScreen(
    puzzleProgressList: List<PuzzleProgress>,
    onPuzzleClick: (Int) -> Unit,
) {
    MainContent(puzzleProgressList, onPuzzleClick)
}

@Composable
fun MainContent(
    puzzleProgressList: List<PuzzleProgress>,
    onPuzzleClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // Adjust the number of columns as needed
            modifier = Modifier.padding(16.dp),
        ) {
            items(puzzleProgressList) { puzzleProgress ->
                PuzzleItem(
                    puzzleProgress = puzzleProgress,
                    onClick = { onPuzzleClick(puzzleProgress.puzzleId) },
                )
            }
        }
    }
}

@Composable
fun PuzzleItem(
    puzzleProgress: PuzzleProgress,
    onClick: () -> Unit,
) {
    val progress = puzzleProgress.completedParts / puzzleProgress.totalParts.toFloat()

    Box(
        modifier =
            Modifier
                .padding(16.dp)
                .size(100.dp)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Draw circular progress indicator around the puzzle number
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(100.dp),
            strokeWidth = 8.dp,
            trackColor =  Color.DarkGray,
        )

        // Display the puzzle number in the center
        Text(
            text = "${puzzleProgress.puzzleId}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PuzzleSelectionScreenPreview() {
    val puzzleProgress = PuzzleProgress(puzzleId = 1, completedParts = 2, totalParts = 5)
    PuzzleItem(puzzleProgress = puzzleProgress) {
    }
}
