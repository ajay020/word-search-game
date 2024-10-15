@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
    grid: List<List<Char>>,
    wordList: List<String>
) {
    val viewModel: SearchGridViewModel = viewModel()

    // Set the words to find
    LaunchedEffect(Unit) {
        viewModel.setWords(wordList)
        viewModel.initGrid(grid)
    }

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier =
            Modifier
                .weight(1f)
                .border(2.dp, Color.Yellow)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            if(viewModel.grid.isNotEmpty()){
                MainContent(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                )
            }else{
                Box {
                    Text(text = "Loading...")
                }
            }
        }
        // Placeholder for additional content (e.g., ads)
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Gray),
        ) {
            // Add your ad content here
            Text(text = "Show ads here")
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
                .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val maxWidth = with(density) { maxWidth.toPx() }
        val maxHeight = with(density) { maxHeight.toPx() }

        // Calculate cellSize based on available space and number of cells
        // Limit the maximum cell size to ensure the grid isn't too large on big screens
        val maxCellSize = with(density) { 100.dp.toPx() }
        val cellSize =
            min(
                min(maxWidth / viewModel.cols, maxHeight / viewModel.rows),
                maxCellSize,
            )

        val gridWidth = cellSize * viewModel.cols
        val gridHeight = cellSize * viewModel.rows

        Column(
            modifier = Modifier.width(with(density) { gridWidth.toDp() }),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Word List
            WordList(
                modifier =
                    Modifier
                        .width(gridWidth.dp)
                        .background(Color.LightGray),
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
                            .border(2.dp, Color.Red),
                ) {
                    // Draw grid
                    for (i in 0 until viewModel.rows) {
                        for (j in 0 until viewModel.cols) {
                            drawRect(
                                color =
                                    if (Pair(i, j) in viewModel.selectedCells) {
                                        Color.LightGray
                                    } else {
                                        Color.White
                                    },
                                topLeft = Offset(j * cellSize, i * cellSize),
                                size =
                                    Size(
                                        cellSize,
                                        cellSize,
                                    ),
                            )
                        }
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
                                    viewModel.rows,
                                    viewModel.cols,
                                    cellSize,
                                    strokeWidth,
                                )
                            val constrainedEnd =
                                constrainToDirection(
                                    startOffset,
                                    end,
                                    direction,
                                    viewModel.rows,
                                    viewModel.cols,
                                    cellSize,
                                    strokeWidth,
                                )

                            drawLine(
                                color = Color.Red.copy(alpha = 0.5f),
                                start = constrainedStart,
                                end = constrainedEnd,
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round,
                            )
                            viewModel.updateSelectedCells(constrainedEnd, cellSize)
                        }
                    }

                    // Draw letters
                    for (i in 0 until viewModel.rows) {
                        for (j in 0 until viewModel.cols) {
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
    heightDp =619,
)
@Composable
private fun WordSearchPreview() {
    val grid = listOf(
        listOf('A', 'C', 'T', 'X', 'X', 'X'),
        listOf('X', 'B', 'A', 'T', 'X', 'X'),
        listOf('X', 'X', 'C', 'A', 'T', 'X'),
        listOf('X', 'X', 'X', 'D', 'O', 'G'),
        listOf('X', 'X', 'X', 'X', 'X', 'X'),
        listOf('X', 'X', 'X', 'X', 'X', 'X')
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
        wordList = wordList
    )
}
