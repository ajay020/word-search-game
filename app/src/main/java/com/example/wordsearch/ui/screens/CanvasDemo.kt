@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wordsearch.utils.GridUtils.calculateSelectedCells

@Suppress("ktlint:standard:function-naming")
@Composable
fun WordSearchGrid(
    modifier: Modifier = Modifier,
    gridSize: Int,
    cellSize: Int,
) {
    var startCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var endCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    val animatedEndCell by animateOffsetAsState(
        targetValue =
            endCell?.let {
                Offset(
                    it.second * cellSize.toFloat(),
                    it.first * cellSize.toFloat(),
                )
            }
                ?: Offset.Zero,
        animationSpec = tween(durationMillis = 100),
        label = "",
    )

    val lineAlpha by animateFloatAsState(
        targetValue = if (startCell != null) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
    )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Cyan),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .background(Color.Gray)
                    .size((gridSize * cellSize).dp),
        ) {
            // Draw the grid
            Column {
                for (row in 0 until gridSize) {
                    Row {
                        for (col in 0 until gridSize) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(cellSize.dp)
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragStart = { offset ->
                                                    startCell = Pair(row, col)
                                                    endCell = Pair(row, col)
                                                    selectedCells = setOf(Pair(row, col))
                                                    Log.d(
                                                        "WordSearchGrid",
                                                        "Drag: offset=$offset",
                                                    )
                                                },
                                                onDrag = { change, _ ->
                                                    val newRow = (change.position.y / cellSize).toInt()
                                                    val newCol = (change.position.x / cellSize).toInt()

                                                    Log.d(
                                                        "WordSearchGrid",
                                                        "Drag: offset=${change.position} $newRow $newCol",
                                                    )

                                                    if (newRow in 0 until gridSize && newCol in 0 until gridSize) {
                                                        endCell = Pair(newRow, newCol)
                                                        selectedCells =
                                                            calculateSelectedCells(
                                                                startCell!!,
                                                                endCell!!,
                                                                gridSize,
                                                                gridSize
                                                            )
                                                    }
                                                },
                                                onDragEnd = {
                                                    // Here you would check if the selected cells form a valid word
                                                    // If not, reset the selection
                                                    startCell = null
                                                    endCell = null
                                                    selectedCells = emptySet()
                                                },
                                            )
                                        },
                            ) {
                                Text(
                                    text = if (Pair(row, col) in selectedCells) "X" else "O",
                                    modifier = Modifier.padding(4.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Draw the selection line
            Canvas(modifier = Modifier.fillMaxSize()) {
                startCell?.let { start ->
                    val startOffset =
                        Offset(
                            start.second * cellSize.toFloat() + cellSize / 2,
                            start.first * cellSize.toFloat() + cellSize / 2,
                        )
                    drawLine(
                        color = Color.Red.copy(alpha = lineAlpha),
                        start = startOffset,
                        end = animatedEndCell + Offset(cellSize / 2f, cellSize / 2f),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
private fun WordSearchPreview() {
    WordSearchGrid(gridSize = 6, cellSize = 40)
}
