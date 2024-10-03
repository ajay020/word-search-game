package com.example.wordsearch.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.data.GridContainerData
import com.example.wordsearch.data.Line
import com.example.wordsearch.ui.viewModels.WordGridViewModel

const val TAG = "WordGrid"

@Composable
fun WordGrid(
    wordList: List<String>,
    grid: List<List<Char>>,
    viewModel: WordGridViewModel = viewModel(),
) {
    val words by viewModel.wordListState.collectAsState()
    val foundWords by viewModel.foundWords.collectAsState()
    val currentWord by viewModel.currentWord.collectAsState()
    val selectedCells by viewModel.selectedCells.collectAsState()
    val selectedLines by viewModel.selectedLines.collectAsState()
    val currentLine by viewModel.currentLine.collectAsState()
    val positionOfHintWord by viewModel.positionOfHintWord.collectAsState()

    LaunchedEffect(wordList, grid) {
        viewModel.initGrid(wordList, grid)
    }

    // Use LocalConfiguration to get screen dimensions
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    val numColumns = grid[0].size

    Log.d(TAG, " cell size:  ${screenWidthDp/ numColumns}")

    // Define breakpoints and adjust cell size and text size based on screen width
    val cellSize =
        when {
            screenWidthDp < 600.dp -> screenWidthDp / numColumns
            else -> 56.dp // Large screen size
        }

    val textSize =
        when {
            numColumns <= 6 -> 22.sp // Small screen text size
            numColumns <= 8 -> 18.sp // Medium screen text size
            numColumns <= 10 -> 16.sp // Medium screen text size
            else -> 14.sp // Large screen text size
        }

    Log.d(TAG, "cellSize: $cellSize textSize: $textSize screenWidth: $screenWidthDp")

    Column(
        modifier =
            Modifier
                .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WordList(
            modifier = Modifier.width(cellSize * grid.size),
            words = words,
            textSize = textSize,
            foundWords = foundWords,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CurrentWord(currentWord = currentWord)
        Spacer(modifier = Modifier.height(8.dp))

        if (grid.isNotEmpty()) {
            GridContainer(
                textSize = textSize,
                positionOfHintWord = positionOfHintWord,
                gridData =
                    GridContainerData(
                        cellSize = cellSize,
                        grid = grid,
                        selectedLines = selectedLines,
                        currentLine = currentLine,
                        selectedCells = selectedCells,
                    ),
                viewModel = viewModel,
            )
        }
    }
}

@Composable
fun GridContainer(
    gridData: GridContainerData,
    viewModel: WordGridViewModel,
    textSize: TextUnit,
    positionOfHintWord: Pair<Int, Int>?,
) {
    val cellSizePx = with(LocalDensity.current) { gridData.cellSize.toPx() }

    Box(
        modifier =
            Modifier
                .size(gridData.cellSize * gridData.grid[0].size, gridData.cellSize * gridData.grid.size)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .drawLines(gridData.selectedLines, gridData.currentLine)
                .border(1.dp, Color.Black)
                .handleGestures(viewModel, cellSizePx),
        contentAlignment = Alignment.Center,
    ) {
        GridDisplay(
            grid = gridData.grid,
            cellSize = gridData.cellSize,
            textSize = textSize,
            selectedCells = gridData.selectedCells,
            positionOfHintWord = positionOfHintWord,
        )
    }
}

@Composable
fun GridDisplay(
    grid: List<List<Char>>,
    textSize: TextUnit,
    cellSize: Dp,
    selectedCells: Set<Pair<Int, Int>>,
    positionOfHintWord: Pair<Int, Int>?,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        grid.forEachIndexed { rowIndex, row ->
            Row {
                row.forEachIndexed { colIndex, letter ->
                    Box(
                        modifier =
                            Modifier
                                .size(cellSize)
                                .then(getCellBorder(rowIndex, colIndex, positionOfHintWord)),
                        contentAlignment = Alignment.Center,
                    ) {
                        val isSelected =
                            selectedCells.contains(rowIndex to colIndex)
                        Text(
                            modifier =
                                Modifier
                                    .padding(0.dp),
                            text = letter.toString(),
                            color = if (isSelected) Color.White else Color.Black,
                            style =
                                TextStyle(
                                    fontSize = textSize,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                }
            }
        }
    }
}

fun getCellBorder(
    rowIndex: Int,
    colIndex: Int,
    positionOfHintWord: Pair<Int, Int>?,
): Modifier =
    positionOfHintWord?.let { (hintRow, hintCol) ->
        if (hintRow == rowIndex && hintCol == colIndex) {
            Modifier.border(2.dp, Color.Black, shape = CircleShape)
        } else {
            Modifier
        }
    } ?: Modifier

@Composable
fun Modifier.drawLines(
    selectedLines: List<Line>,
    currentLine: Line?,
): Modifier =
    drawBehind {
        selectedLines.forEach { line ->
            drawLinePath(line.offsets, line.color)
        }
//         Drawing the currently dragged line (if needed)
        currentLine?.let {
            drawLinePath(currentLine.offsets, currentLine.color)
        }
    }

fun DrawScope.drawLinePath(
    points: List<Offset>,
    color: Color,
) {
    val path =
        Path().apply {
            moveTo(points.first().x, points.first().y)
            for (point in points.drop(1)) {
                lineTo(point.x, point.y)
            }
        }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 60f, cap = StrokeCap.Round),
    )
}

fun Modifier.handleGestures(
    viewModel: WordGridViewModel,
    cellSizePx: Float,
): Modifier =
    pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset -> viewModel.onDragStart(offset, cellSizePx) },
            onDragEnd = { viewModel.onDragEnd() },
            onDrag = { change, _ -> viewModel.onDrag(change.position, cellSizePx) },
        )
    }

@Composable
fun CurrentWord(
    modifier: Modifier = Modifier,
    currentWord: String = " ",
) {
    Box(
        modifier =
            modifier
                .height(50.dp)
                .padding(8.dp),
    ) {
        if (currentWord.isEmpty()) return
        Text(
            modifier =
                modifier
                    .background(Color.Blue, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            text = currentWord,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordList(
    modifier: Modifier = Modifier,
    words: List<String>,
    foundWords: Set<String>,
    textSize: TextUnit,
) {
    FlowRow(
        modifier =
            modifier
                .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
        maxItemsInEachRow = 4,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        words.forEach { word ->
            val isFound = word in foundWords

            Text(
                modifier = Modifier.padding(8.dp),
                text = word,
                style =
                    TextStyle(
                        color = if (isFound) Color.Gray else Color.Black,
                        fontSize = textSize,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WordGridPreview() {
    val grid =
        listOf(
            listOf('A', 'B', 'C', 'D'),
            listOf('A', 'B', 'C', 'D'),
            listOf('A', 'B', 'C', 'D'),
            listOf('A', 'B', 'C', 'D'),
        )
    val words =
        listOf(
            "APPLE",
            "CAT",
        )

    val viewModel = WordGridViewModel()

    WordGrid(
        viewModel = viewModel,
        grid = grid,
        wordList = words,
    )

//    WordList(words = words, foundWords = setOf("APPLE"), textSize = 16.sp)
}
