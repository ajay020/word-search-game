@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordsearch.ui.viewModels.WordSearchViewModel
import com.example.wordsearch.utils.GridUtils.calculateSelectedCells
import com.example.wordsearch.utils.GridUtils.getCellCenter
import com.example.wordsearch.utils.GridUtils.interpolatePoints
import com.example.wordsearch.utils.GridUtils.isStraightLine
import com.example.wordsearch.utils.GridUtils.offsetToGridCoordinate

const val TAG = "WordSearchScreen"

@Suppress("ktlint:standard:function-naming")
@Composable
fun WordSearchScreen(
    viewModel: WordSearchViewModel,
    modifier: Modifier,
) {
    val gridState by viewModel.gridState.collectAsState()
    val wordListState by viewModel.wordListState.collectAsState()

    ScreenContent(
        modifier = modifier,
        grid = gridState,
        words = wordListState,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    grid: List<List<Char>>,
    words: List<String>,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
    ) {
        WordGrid(grid = grid, words = words)
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun WordGrid(
    grid: List<List<Char>>,
    words: List<String>,
) {
    var selectedCells by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }
    var foundWords by remember { mutableStateOf(setOf<String>()) }
    var startCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var currentWord by remember { mutableStateOf("") }

    var gridLayoutSize by remember { mutableStateOf(IntSize.Zero) }

    // Canvas state
    var startLineOffset by remember { mutableStateOf(Offset.Zero) }
    var endLineOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedLines by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }
    var currentLine by remember { mutableStateOf<List<Offset>>(emptyList()) }

    // cell size
    val cellSize = 50.dp
    val interpolationSteps = 10 // Number of steps to interpolate between cells

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("cellSize: $cellSize")
//        Spacer(modifier = Modifier.height(24.dp))
        WordList(
            modifier = Modifier.width(cellSize * grid[0].size),
            words,
            foundWords,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CurrentWord(text = currentWord)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
                Modifier
                    .width(cellSize * grid[0].size)
                    .height(cellSize * grid.size)
                    .onGloballyPositioned { coordinates ->
                        gridLayoutSize = coordinates.size
                        Log.d(TAG, "onGloballyPositioned: $gridLayoutSize")
                    }.background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val (row, col) =
                                    offsetToGridCoordinate(
                                        offset,
                                        gridLayoutSize,
                                        grid.size,
                                    )
                                startCell = row to col
                                selectedCells = setOf(row to col)

                                // Set startLineOffset to center of the starting cell
                                startLineOffset = getCellCenter(row, col, cellSize.toPx())
                                endLineOffset = startLineOffset
                                currentLine = listOf(startLineOffset, endLineOffset)

//                                Log.d("WordGrid", "Drag Start: offset=$offset, cell=($row, $col)")
                            },
                            onDragEnd = {
                                startCell?.let { start ->
                                    val endCell = selectedCells.lastOrNull()
                                    if (endCell != null && isStraightLine(start, endCell)) {
                                        // Interpolate cells from start to end
                                        selectedCells =
                                            calculateSelectedCells(start, endCell, grid.size)
                                    }
                                }
//                                Log.d("WordGrid", "Drag End: selectedCells=$selectedCells")

                                val selectedWord =
                                    selectedCells
                                        .map { (r, c) -> grid[r][c] }
                                        .joinToString("")
                                if (words.contains(selectedWord) &&
                                    !foundWords.contains(selectedWord)
                                ) {
                                    foundWords = foundWords + selectedWord
                                    // make last offset to be in the center of the cell
                                    val lastOffset =
                                        getCellCenter(
                                            selectedCells.last().first,
                                            selectedCells.last().second,
                                            cellSize.toPx(),
                                        )
                                    currentLine =
                                        currentLine.dropLast(currentLine.size - 1) + listOf(lastOffset)

                                    selectedLines = selectedLines + listOf(currentLine)
                                }

                                // Reset the state for the next drag
                                startCell = null
                                currentLine = emptyList()
                                selectedCells = setOf()
                                currentWord = ""
                            },
                            onDrag = { change, _ ->
                                // Continuously calculate the cell being dragged over and add to draggedCells
                                val offset = change.position

                                val (row, col) =
                                    offsetToGridCoordinate(
                                        offset,
                                        gridLayoutSize,
                                        grid.size,
                                    )
                                val newCell = row to col
                                endLineOffset = getCellCenter(row, col, cellSize.toPx())

                                selectedCells = calculateSelectedCells(startCell!!, newCell, grid.size)
//                                Log.d("WordGrid", "Drag: offset=$offset, cell=($row, $col)")

                                // Update the current word being typed
                                currentWord =
                                    selectedCells.map { (r, c) -> grid[r][c] }.joinToString("")

                                // Interpolate points between the start and end to smooth the transition
                                if (isStraightLine(startCell!!, Pair(row, col))) {
                                    currentLine =
                                        interpolatePoints(
                                            startLineOffset,
                                            endLineOffset,
                                            interpolationSteps,
                                        )
                                }
                            },
                        )
                    },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                grid.forEachIndexed { rowIndex, row ->
                    Row {
                        row.forEachIndexed { colIndex, letter ->
                            Box(
                                modifier =
                                    Modifier
                                        .size(cellSize),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = letter.toString(),
                                    style =
                                        TextStyle(
                                            color = Color.Black,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                )
                            }
                        }
                    }
                }
            }

            Canvas(
                modifier =
                    Modifier
                        .matchParentSize()
                        .background(Color.Transparent),
            ) {
//                Log.d("WordGrid", "currentLine ${currentLine.joinToString(", ")}")

                if (currentLine.isNotEmpty()) {
                    val path =
                        Path().apply {
                            moveTo(currentLine.first().x, currentLine.first().y)
                            for (point in currentLine.drop(1)) {
                                lineTo(point.x, point.y)
                            }
                        }

                    // Draw the entire path with rounded cap at the ends
                    drawPath(
                        path = path,
                        color = Color.Blue.copy(alpha = 0.3f),
                        style =
                            Stroke(
                                width = 70f,
                                cap = StrokeCap.Round, // This will only affect the start and end of the path
                            ),
                    )
                }
                selectedLines.forEach { line ->
                    val path =
                        Path().apply {
                            moveTo(line.first().x, line.first().y)
                            for (point in line.drop(1)) {
                                lineTo(point.x, point.y)
                            }
                        }

                    drawPath(
                        path = path,
                        color = Color.Blue.copy(alpha = 0.3f),
                        style =
                            Stroke(
                                width = 70f,
                                cap = StrokeCap.Round, // This will only affect the start and end of the path
                            ),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Suppress("ktlint:standard:function-naming")
@Composable
fun WordList(
    modifier: Modifier = Modifier,
    wordList: List<String>,
    foundWords: Set<String>,
) {
    FlowRow(
        modifier =
            modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
    ) {
        wordList.forEach { word ->
            val isFound = word in foundWords

            Text(
                modifier = Modifier.padding(8.dp),
                text = if (isFound) "✅ $word" else word,
                style =
                    TextStyle(
                        color = if (isFound) Color.Green else Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                    ),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun CurrentWord(
    modifier: Modifier = Modifier,
    text: String = " ",
) {
    Box(
        modifier =
            modifier
                .height(50.dp)
                .padding(8.dp),
    ) {
        if (text.isEmpty()) return
        Text(
            modifier =
                modifier
                    .background(Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            text = text,
            style =
                TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                ),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
private fun WordGridPreview() {
    val grid =
        listOf(
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
        )
    val words =
        listOf(
            "APPLE",
            "BANANA",
            "CHERRY",
            "DATE",
            "EGG",
            "FRUIT",
            "GRAPE",
            "FRUIT",
            "GRAPE",
        )
//    WordGrid(grid, words, { _, _ -> })
    ScreenContent(grid = grid, words = words)
}
