package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordsearch.data.PuzzleProgress

// PuzzleScreen to display puzzles for the selected level
@Composable
fun PuzzleSelectionScreen(
    puzzleProgressList: List<PuzzleProgress>,
    onPuzzleClick: (Int, Boolean) -> Unit,
) {
    MainContent(puzzleProgressList, onPuzzleClick)
}

@Composable
fun MainContent(
    puzzleProgressList: List<PuzzleProgress>,
    onPuzzleClick: (Int, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(16.dp),
        ) {
            itemsIndexed(puzzleProgressList) { index, puzzleProgress ->
                val isPuzzleLocked =
                    index > 0 &&
                        puzzleProgressList[index - 1].completedParts < puzzleProgressList[index - 1].totalParts
                PuzzleItem(
                    puzzleProgress = puzzleProgress,
                    isLocked = isPuzzleLocked,
                    onClick = {
                        onPuzzleClick(
                            puzzleProgress.puzzleId,
                            isPuzzleLocked,
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun PuzzleItem(
    puzzleProgress: PuzzleProgress,
    isLocked: Boolean,
    onClick: () -> Unit,
) {
    val progress = puzzleProgress.completedParts / puzzleProgress.totalParts.toFloat()
    val isActive = !isLocked && progress < 1f

    Column(
        modifier =
            Modifier
                .padding(8.dp)
                .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .let {
                        if (isActive) {
                            it.clickable(onClick = onClick)
                        } else if (isLocked) {
                            it
                                .clickable(onClick = onClick)
                                .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
                        } else {
                            it.background(Color.Blue.copy(alpha = 0.5f), shape = CircleShape)
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            // Draw circular progress indicator around the puzzle number
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(80.dp),
                strokeWidth = 8.dp,
                trackColor = Color.DarkGray,
                color = Color.Blue,
            )

            if (isLocked) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Default.Lock,
                    tint = Color.DarkGray,
                    contentDescription = "",
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (isActive) {
                        Text(
                            text = "${puzzleProgress.completedParts}/${puzzleProgress.totalParts}",
                            fontSize = 16.sp,
                            color = Color.Black,
                        )
                    } else {
                        Icon(
                            modifier = Modifier.size(38.dp),
                            imageVector = Icons.Default.Star,
                            tint = Color.White,
                            contentDescription = "",
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${puzzleProgress.puzzleId}",
            fontSize = 16.sp,
            color = Color.Black,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PuzzleSelectionScreenPreview() {
    val puzzleProgress = PuzzleProgress(puzzleId = 1, completedParts = 2, totalParts = 5)
//    PuzzleItem(puzzleProgress = puzzleProgress) {
//    }

    val puzzleProgressList =
        listOf(
            PuzzleProgress(puzzleId = 1, completedParts = 5, totalParts = 5),
            PuzzleProgress(puzzleId = 2, completedParts = 3, totalParts = 5),
            PuzzleProgress(puzzleId = 3, completedParts = 0, totalParts = 5),
        )

    MainContent(puzzleProgressList = puzzleProgressList, onPuzzleClick = { _, _ -> })
}
