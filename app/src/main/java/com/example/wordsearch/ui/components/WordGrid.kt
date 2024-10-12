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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.wordsearch.utils.GridUtils.generateGrid

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
    val positionOfHintWords by viewModel.positionOfHintWords.collectAsState()

    LaunchedEffect(wordList, grid) {
        viewModel.initGrid(wordList, grid)
    }

    // Use LocalConfiguration to get screen dimensions
    val configuration = LocalConfiguration.current
//    val screenWidthDp = configuration.screenWidthDp.dp
    val padding = 8.dp // for 8.dp padding on each side

    val screenWidthDp = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    Log.d(TAG, "screenWidth: $screenWidthDp screenHeight: ${configuration.screenHeightDp}")

    // Recalculate cell size when the grid changes
    val cellSize by remember(grid) {
        derivedStateOf {
            val numColumns = grid.getOrNull(0)?.size ?: 1
            when {
                screenWidthDp > 600.dp -> minOf((screenWidthDp - padding) / numColumns, 54.dp)
                else -> (screenWidthDp - padding) / numColumns
            }
        }
    }

    // Adjust text size based on the number of columns
    val textSize by remember(grid) {
        derivedStateOf {
            (cellSize.value / 2).sp // This scales text size proportionally to cell size
        }
    }
    // Get LocalDensity from the composable context
    val density = LocalDensity.current

    LaunchedEffect(cellSize) {
        viewModel.updateCellSizePx((density.run { cellSize.toPx() }))
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        WordList(
            modifier = Modifier.width(cellSize * grid[0].size),
            words = words,
            foundWords = foundWords,
        )
        CurrentWord(currentWord = currentWord)

        if (grid.isNotEmpty()) {
            GridContainer(
                textSize = textSize,
                positionOfHintWords = positionOfHintWords,
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
    positionOfHintWords: List<Pair<Int, Int>>,
) {
    Box(
        modifier =
            Modifier
                .size(
                    gridData.cellSize * gridData.grid[0].size,
                    gridData.cellSize * gridData.grid.size,
                ).border(1.dp, Color.Red)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .drawLines(gridData.selectedLines, gridData.currentLine)
                .handleGestures(viewModel),
        contentAlignment = Alignment.Center,
    ) {
        GridDisplay(
            grid = gridData.grid,
            cellSize = gridData.cellSize,
            textSize = textSize,
            selectedCells = gridData.selectedCells,
            positionOfHintWords = positionOfHintWords,
        )
    }
}

@Composable
fun GridDisplay(
    grid: List<List<Char>>,
    textSize: TextUnit,
    cellSize: Dp,
    selectedCells: Set<Pair<Int, Int>>,
    positionOfHintWords: List<Pair<Int, Int>>,
) {
    Column(
        modifier =
            Modifier
                .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        grid.forEachIndexed { rowIndex, row ->
            Row(
                modifier =
                    Modifier
//                    .border(1.dp, Color.Black)
                        .fillMaxWidth(),
            ) {
                row.forEachIndexed { colIndex, letter ->
                    val isHintWord = positionOfHintWords.contains(rowIndex to colIndex)

                    Box(
                        modifier =
                            Modifier
//                                .border(1.dp, Color.Black)
                                .getBackground(isHintWord)
                                .size(cellSize),
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

fun Modifier.getBackground(isHintWord: Boolean): Modifier =
    if (isHintWord) {
        this.background(Color.Yellow, shape = CircleShape)
    } else {
        this
    }

@Composable
fun Modifier.drawLines(
    selectedLines: List<Line>,
    currentLine: Line?,
): Modifier =
    drawBehind {
        selectedLines.forEach { line ->
            drawLinePath(strokeWidth = 60f, line.offsets, line.color)
        }
//         Drawing the currently dragged line (if needed)
        currentLine?.let {
            drawLinePath(strokeWidth = 60f, currentLine.offsets, currentLine.color)
        }
    }

fun DrawScope.drawLinePath(
    strokeWidth: Float = 60f,
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
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )
}

fun Modifier.handleGestures(viewModel: WordGridViewModel): Modifier =
    pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset -> viewModel.onDragStart(offset) },
            onDragEnd = { viewModel.onDragEnd() },
            onDrag = { change, _ -> viewModel.onDrag(change.position) },
        )
    }

@Composable
fun CurrentWord(
    modifier: Modifier = Modifier,
    currentWord: String,
) {
    Box(
        modifier =
            modifier
                .height(30.dp),
    ) {
        if (currentWord.isNotEmpty()) {
            Text(
                modifier =
                    modifier
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(8.dp),
                        ).padding(horizontal = 8.dp, vertical = 2.dp),
                text = currentWord,
                style =
                    TextStyle(
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    ),
            )
        } else {
            Spacer(modifier = Modifier.height(0.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordList(
    modifier: Modifier = Modifier,
    words: List<String>,
    foundWords: Set<String>,
) {
    FlowRow(
        modifier =
            modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(0.dp),
        maxItemsInEachRow = 4,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        words.forEach { word ->
            val isFound = word in foundWords

            Text(
                modifier = Modifier.padding(8.dp),
                text = word,
                style =
                    TextStyle(
                        color = if (isFound) Color.Gray else Color.Black,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None,
                    ),
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun WordGridPreview() {
    val words =
        listOf(
          "MAT", "BAT", "CAT", "PAT", "POT", "NET"
        )

    val viewModel = WordGridViewModel()

//    WordList(
//        words = words,
//        foundWords = setOf("APPLE"), textSize = 16.sp
//    )

    WordGrid(
        viewModel = viewModel,
        grid = generateGrid(words),
        wordList = words,
    )
}
