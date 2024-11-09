@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.wordsearch.viewModels.SearchGridState
import com.example.wordsearch.viewModels.SearchGridViewModel
import com.example.wordsearch.viewModels.Word
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

@Composable
fun SearchGrid(
    modifier: Modifier,
    viewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
    resetTimer: () -> Unit,
    navigateToMainScreen: () -> Unit,
) {
    val uiState = viewModel.uiState.value

    Column(
        modifier =
            modifier
                .padding(4.dp)
                .background(Color.Gray),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MainContent(
            modifier = Modifier,
            uiState = uiState,
            onDragStart = { offset: Offset, cellSize: Float ->
                viewModel.onDragStart(
                    offset,
                    cellSize,
                )
            },
            onDrag = { viewModel.onDrag(it) },
            onDragEnd = { viewModel.onDragEnd() },
            getCurrentLineColor = { viewModel.getCurrentLineColor() },
            updateSelectedCells = { offset: Offset, cellSize: Float ->
                viewModel.updateSelectedCells(offset, cellSize)
            },
        )

        if (uiState.showCompletionDialog) {
            PuzzleCompletionDialog(
                onDismiss = {
                    viewModel.onDismissDialog()
                    navigateToMainScreen()
                },
                onNextPuzzle = {
                    viewModel.loadNextPuzzle()
                    resetTimer()
                },
            )
        }
    }
}

@Suppress("LongParameterList")
@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    uiState: SearchGridState,
    onDragStart: (Offset, Float) -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Offset) -> Unit,
    getCurrentLineColor: () -> Color = { Color.Green },
    updateSelectedCells: (Offset, Float) -> Unit = { _, _ -> },
) {
    BoxWithConstraints(
        modifier =
            modifier
                .background(Color.White.copy(alpha = 0.5f))
                .wrapContentSize(),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val maxWidth = with(density) { maxWidth.toPx() }
        val maxHeight = with(density) { maxHeight.toPx() }

        // Calculate cellSize based on available space and number of cells
        // Limit the maximum cell size to ensure the grid isn't too large on big screens
//        val maxCellSize = with(density) { 60.dp.toPx() }

        // Use mutableStateOf for cellSize so it triggers recomposition when it changes
        var cellSize by remember { mutableFloatStateOf(with(density) { 60.dp.toPx() }) }

        LaunchedEffect(uiState.grid.size, maxWidth, maxHeight) {
            cellSize =
                min(
                    maxWidth / uiState.grid.size,
                    maxHeight / uiState.grid.size,
                )
        }

        val rows = uiState.grid.size
        val cols = uiState.grid.size

        val gridWidth = cellSize * cols
        val gridHeight = cellSize * rows

        Column(
            modifier =
                Modifier
//                .background(Color.White.copy(alpha = 0.2f))
                    .width(with(density) { gridWidth.toDp() }),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Word List
            WordList(
                modifier =
                    Modifier
                        .width(gridWidth.dp)
                        .background(Color.Transparent),
                words = uiState.words,
            )

            Box(
                modifier =
                    Modifier
                        .background(Color.White.copy(alpha = 0.5f))
                        .size(
                            with(density) {
                                gridWidth.toDp()
                            },
                            with(density) {
                                gridHeight.toDp()
                            },
                        ).pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> onDragStart(offset, cellSize) },
                                onDragEnd = { onDragEnd() },
                                onDrag = { change, _ -> onDrag(change.position) },
                            )
                        },
            ) {
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                ) {
                    // Draw grid
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            drawRect(
                                color = Color.Transparent,
                                topLeft = Offset(j * cellSize, i * cellSize),
                                size =
                                    Size(
                                        cellSize,
                                        cellSize,
                                    ),
                            )

                            drawCircle(
                                color =
                                    if (Pair(i, j) in uiState.positionOfHintWords) {
                                        getCurrentLineColor()
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

                    // Draw lines for previously words
                    uiState.foundWords.forEach { foundWord ->
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
                                    width = cellSize * 3 / 4,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Bevel,
                                ),
                        )
                    }

                    // Draw selection line and calculate selected cells
                    uiState.startCell?.let { start ->
                        uiState.currentDragPosition?.let { end ->
                            val startOffset =
                                Offset(
                                    start.second * cellSize + cellSize / 2,
                                    start.first * cellSize + cellSize / 2,
                                )
                            val direction = getDirection(startOffset, end)
                            val strokeWidth = cellSize * 3 / 4
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
                                color = getCurrentLineColor(),
                                start = constrainedStart,
                                end = constrainedEnd,
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round,
                            )
                            updateSelectedCells(constrainedEnd, cellSize)
                        }
                    }

                    // Draw letters
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            drawContext.canvas.nativeCanvas.drawText(
                                uiState.grid[i][j].toString(),
                                j * cellSize + cellSize / 3,
                                i * cellSize + 2 * cellSize / 3,
                                android.graphics.Paint().apply {
                                    color =
                                        if (Pair(i, j) in uiState.selectedCells
                                        ) {
                                            android.graphics.Color.WHITE
                                        } else {
                                            android.graphics.Color.BLACK
                                        }
                                    textSize = cellSize * 0.5f
                                    isFakeBoldText = true
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

@Suppress("LongParameterList")
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
        )
    val wordList =
        listOf(
            "ACT",
            "BAT",
            "CAT",
            "DOG",
        )

    val words = wordList.map { Word(it, false) }

    MainContent(
        uiState =
            SearchGridState(
                grid = grid,
                words = words,
            ),
        onDragStart = { a, b -> },
        onDragEnd = { /*TODO*/ },
        onDrag = {},
    )
}
