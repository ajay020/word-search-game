@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

@Composable
fun WordSearchGame() {
    val rows = 8
    val cols = 7
    val grid = remember { generateGrid(rows, cols) }
    val wordList = listOf(
        "WORD", "BOOK", "CAT", "DOG", "EGG", "FLAG", "HOME"
    )
    var selectedCells by remember { mutableStateOf(listOf<Pair<Int, Int>>()) }
    var startCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var endCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var currentDragPosition by remember { mutableStateOf<Offset?>(null) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .border(1.dp, Color.Yellow)
                .background(Color.Gray)
                .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Word Search Game")

        BoxWithConstraints(
            modifier =
                Modifier
                    .border(5.dp, Color.Blue)
                    .background(Color.Yellow)
                    .padding(0.dp)
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val canvasWidth = with(LocalDensity.current) { maxWidth.toPx() }
            val canvasHeight = with(LocalDensity.current) { maxHeight.toPx() }
            val cellSize = min(canvasWidth / cols, canvasHeight / rows)

            Box(
                modifier =
                    Modifier
                        .size(cellSize.dp * cols, cellSize.dp * rows)
                        .border(2.dp, Color.Green)
                        .background(Color.Cyan)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    Log.d("canvas", "cellSize: $cellSize")
                                    startCell =
                                        Pair(
                                            (offset.y / cellSize).toInt(),
                                            (offset.x / cellSize).toInt(),
                                        )
                                    endCell = startCell
                                    currentDragPosition = offset
                                    selectedCells = listOf(startCell!!)
                                },
                                onDragEnd = {
                                    startCell = null
                                    endCell = null
                                    currentDragPosition = null
                                    selectedCells = emptyList()
                                },
                                onDrag = { change, _ ->
                                    currentDragPosition = change.position
                                },
                            )
                        },
                contentAlignment = Alignment.TopStart,
            ) {
                Canvas(
                    modifier =
                        Modifier
                            .padding(0.dp)
                            .background(Color.Blue)
                            .border(14.dp, Color.Red)
                            .fillMaxSize()
                    ,
                ) {
                    // Draw grid
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            drawRect(
                                color =
                                    if (Pair(i, j) in selectedCells) {
                                        Color.White
                                    } else {
                                        Color.White
                                    },
                                topLeft = Offset(j * cellSize, i * cellSize),
                                size = Size(cellSize, cellSize),
                            )
                        }
                    }

                    // Draw selection line
                    if (startCell != null && currentDragPosition != null) {
                        val start =
                            Offset(
                                startCell!!.second * cellSize + cellSize / 2,
                                startCell!!.first * cellSize + cellSize / 2,
                            )
                        val end = currentDragPosition!!

                        val direction = getDirection(start, end)
                        val strokeWidth = cellSize / 2
                        val constrainedStart =
                            constrainToDirection(
                                start,
                                start,
                                direction,
                                rows,
                                cols,
                                cellSize,
                                strokeWidth,
                            )
                        val constrainedEnd =
                            constrainToDirection(
                                start,
                                end,
                                direction,
                                rows,
                                cols,
                                cellSize,
                                strokeWidth,
                            )

                        drawLine(
                            color = Color.Red.copy(0.6f),
                            start = constrainedStart,
                            end = constrainedEnd,
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round,
                        )
                        // Calculate endCell based on constrainedEnd
                        endCell = Pair(
                            (constrainedEnd.y / cellSize).toInt(),
                            (constrainedEnd.x / cellSize).toInt()
                        )

                        // Update selectedCells based on startCell and endCell
                        selectedCells = getSelectedCells(startCell!!, endCell!!, rows, cols)
                    }


                    // Draw letters
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            drawContext.canvas.nativeCanvas.drawText(
                                grid[i][j].toString(),
                                j * cellSize + cellSize / 3,
                                i * cellSize + 2 * cellSize / 3,
                                android.graphics.Paint().apply {
                                    color = if(Pair(i, j) in selectedCells){
                                        android.graphics.Color.WHITE
                                    }else{
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

enum class Direction { HORIZONTAL, VERTICAL, DIAGONAL }

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
        Direction.HORIZONTAL ->
            Offset(
                x = constrainX(end.x),
                y = constrainY(start.y),
            )

        Direction.VERTICAL ->
            Offset(
                x = constrainX(start.x),
                y = constrainY(end.y),
            )

        Direction.DIAGONAL -> {
            val distance = minOf(abs(dx), abs(dy))
            Offset(
                x = constrainX(start.x + distance * dx.sign),
                y = constrainY(start.y + distance * dy.sign),
            )
        }
    }
}

fun generateGrid(
    rows: Int,
    cols: Int,
): List<List<Char>> = List(rows) { List(cols) { ('A'..'Z').random() } }

fun getSelectedCells(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    rows: Int,
    cols: Int,
): List<Pair<Int, Int>> {
    val dx = end.first - start.first
    val dy = end.second - start.second

    return when {
        dx == 0 ->
            (minOf(start.second, end.second)..maxOf(start.second, end.second)).map {
                Pair(
                    start.first,
                    it,
                )
            }

        dy == 0 ->
            (minOf(start.first, end.first)..maxOf(start.first, end.first)).map {
                Pair(
                    it,
                    start.second,
                )
            }

        abs(dx) == abs(dy) -> {
            val xRange = if (dx > 0) start.first..end.first else start.first downTo end.first
            val yRange = if (dy > 0) start.second..end.second else start.second downTo end.second
            xRange.zip(yRange).toList()
        }

        else -> listOf(start)
    }.filter { it.first in 0 until rows && it.second in 0 until cols }
}

@Suppress("ktlint:standard:function-naming")
@Preview(
    showBackground = true,
//    widthDp = 369,
//    heightDp = 619,
)
@Composable
private fun WordSearchPreview() {
    WordSearchGame()
}
