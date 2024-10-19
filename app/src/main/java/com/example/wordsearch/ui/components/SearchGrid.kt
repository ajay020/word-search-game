@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.viewModels.SearchGridViewModel
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

@Composable
fun SearchGrid(
    grid: List<List<Char>> = emptyList(),
    wordList: List<String> = emptyList(),
    viewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.LightGray),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(1.dp, Color.Yellow)
                    .padding(0.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            MainContent(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
            )

            if (viewModel.showCompletionDialog) {
                PuzzleCompletionDialog(
                    onDismiss = { viewModel.onDismissDialog() },
                    onNextPuzzle = { viewModel.loadNextPuzzle() },
                )
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: SearchGridViewModel,
) {
    BoxWithConstraints(
        modifier =
            modifier
                .border(2.dp, Color.DarkGray)
//                .wrapContentSize()
                .background(Color.Cyan),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val maxWidth = with(density) { maxWidth.toPx() }
        val maxHeight = with(density) { maxHeight.toPx() }

        // Calculate cellSize based on available space and number of cells
        // Limit the maximum cell size to ensure the grid isn't too large on big screens
        val maxCellSize = with(density) { 60.dp.toPx() }

        val cellSize by remember(viewModel.grid.size) {
            derivedStateOf {
                min(
                    maxWidth / viewModel.grid.size,
                    maxHeight / viewModel.grid.size,
                )
            }
        }

        val rows = viewModel.grid.size
        val cols = viewModel.grid.size

        val gridWidth = cellSize * cols
        val gridHeight = cellSize * rows

        Log.d("SearchGrid", "cellSize: $cellSize ${maxWidth / cols} ${maxHeight / rows}")
        Log.d("SearchGrid", "mWidth: $maxWidth mHeight:  $maxHeight")
        Log.d("SearchGrid", "gridWidth: $gridWidth gridHeight:  $gridHeight")

        Column(
            modifier = Modifier.width(with(density) { gridWidth.toDp() }),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Word List
            WordList(
                modifier =
                    Modifier
                        .width(gridWidth.dp)
                        .background(Color.White),
                words = viewModel.words,
            )

            Box(
                modifier =
                    Modifier
                        .size(with(density) { gridWidth.toDp() }, with(density) { gridHeight.toDp() })
                        .background(Color.Cyan)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> viewModel.onDragStart(offset, cellSize) },
                                onDragEnd = { viewModel.onDragEnd() },
                                onDrag = { change, _ -> viewModel.onDrag(change.position) },
                            )
                        },
            ) {
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.Red),
                ) {
                    val rows = viewModel.grid.size
                    val cols = viewModel.grid.size

                    // Draw grid
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(j * cellSize, i * cellSize),
                                size =
                                    Size(
                                        cellSize,
                                        cellSize,
                                    ),
                            )

                            drawCircle(
                                color =
                                    if (Pair(i, j) in viewModel.positionOfHintWords) {
                                        Color.Red
                                    } else {
                                        Color.Transparent
                                    },
                                center =
                                    Offset(
                                        j * cellSize + cellSize / 2,
                                        i * cellSize + cellSize / 2,
                                    ),
                                radius = cellSize / 4,
                            )
                        }
                    }

                    viewModel.foundWords.forEach { foundWord ->
                        val path =
                            Path().apply {
                                val startCell = foundWord.cells.first()
                                moveTo(
                                    startCell.second * cellSize + cellSize / 2,
                                    startCell.first * cellSize + cellSize / 2,
                                )
                                foundWord.cells.forEach { cell ->
                                    lineTo(
                                        cell.second * cellSize + cellSize / 2,
                                        cell.first * cellSize + cellSize / 2,
                                    )
                                }
                            }
                        drawPath(
                            path,
                            color = foundWord.color,
                            style =
                                Stroke(
                                    width = cellSize / 2,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Bevel,
                                ),
                        )
                    }

                    // Draw selection line and calculate selected cells
                    viewModel.startCell?.let { start ->
                        viewModel.currentDragPosition?.let { end ->
                            val startOffset =
                                Offset(
                                    start.second * cellSize + cellSize / 2,
                                    start.first * cellSize + cellSize / 2,
                                )
                            val direction = getDirection(startOffset, end)
                            val strokeWidth = cellSize / 2
                            val constrainedStart =
                                constrainToDirection(
                                    startOffset,
                                    startOffset,
                                    direction,
                                    rows,
                                    cols,
                                    cellSize,
                                    strokeWidth,
                                )
                            val constrainedEnd =
                                constrainToDirection(
                                    startOffset,
                                    end,
                                    direction,
                                    rows,
                                    cols,
                                    cellSize,
                                    strokeWidth,
                                )

                            drawLine(
                                color = viewModel.getCurrentLineColor(),
                                start = constrainedStart,
                                end = constrainedEnd,
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round,
                            )
                            viewModel.updateSelectedCells(constrainedEnd, cellSize)
                        }
                    }

                    // Draw letters
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            drawContext.canvas.nativeCanvas.drawText(
                                viewModel.grid[i][j].toString(),
                                j * cellSize + cellSize / 3,
                                i * cellSize + 2 * cellSize / 3,
                                android.graphics.Paint().apply {
                                    color =
                                        if (Pair(i, j) in viewModel.selectedCells
                                        ) {
                                            android.graphics.Color.WHITE
                                        } else {
                                            android.graphics.Color.BLACK
                                        }
                                    textSize = cellSize * 0.5f
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzleCompletionDialog(
    onDismiss: () -> Unit,
    onNextPuzzle: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Congratulations!") },
        text = { Text("You've completed the puzzle!") },
        confirmButton = {
            Button(onClick = onNextPuzzle) {
                Text("Next Puzzle")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

// Helper functions
fun getDirection(
    start: Offset,
    end: Offset,
): Direction {
    val dx = abs(end.x - start.x)
    val dy = abs(end.y - start.y)
    return when {
        dy < dx / 2 -> Direction.HORIZONTAL
        dx < dy / 2 -> Direction.VERTICAL
        else -> Direction.DIAGONAL
    }
}

fun constrainToDirection(
    start: Offset,
    end: Offset,
    direction: Direction,
    rows: Int,
    cols: Int,
    cellSize: Float,
    strokeWidth: Float,
): Offset {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val halfStroke = strokeWidth / 2

    fun constrainX(x: Float): Float = x.coerceIn(halfStroke, cellSize * cols - halfStroke)

    fun constrainY(y: Float): Float = y.coerceIn(halfStroke, cellSize * rows - halfStroke)

    return when (direction) {
        Direction.HORIZONTAL -> Offset(constrainX(end.x), constrainY(start.y))
        Direction.VERTICAL -> Offset(constrainX(start.x), constrainY(end.y))
        Direction.DIAGONAL -> {
            val distance = minOf(abs(dx), abs(dy))
            Offset(
                constrainX(start.x + distance * dx.sign),
                constrainY(start.y + distance * dy.sign),
            )
        }
    }
}

enum class Direction { HORIZONTAL, VERTICAL, DIAGONAL }

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun WordSearchPreview() {
    val grid =
        listOf(
            listOf('A', 'C', 'T', 'X', 'X'),
            listOf('X', 'B', 'A', 'T', 'X'),
            listOf('X', 'X', 'C', 'A', 'T'),
            listOf('X', 'X', 'X', 'D', 'O'),
            listOf('X', 'X', 'X', 'X', 'X'),
            listOf('X', 'X', 'X', 'X', 'X'),
        )
    val wordList =
        listOf(
            "ACT",
            "BAT",
            "CAT",
            "DOG",
        )

    SearchGrid(
        grid = grid,
        wordList = wordList,
    )
}
