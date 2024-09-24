package com.example.wordsearch.ui.components

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.data.Line
import com.example.wordsearch.ui.viewModels.WordSearchViewModel

const val TAG = "WordGrid"

@Composable
fun WordGrid(viewModel: WordSearchViewModel = viewModel()) {
    val words by viewModel.wordListState.collectAsState()
    val foundWords by viewModel.foundWords.collectAsState()
    val currentWord by viewModel.currentWord.collectAsState()

    // cell size
    val cellSize = 50.dp
    Log.d(TAG, "WordGrid render:")

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WordList(words = words, foundWords = foundWords)
        Spacer(modifier = Modifier.height(8.dp))
        CurrentWord(currentWord = currentWord)
        Spacer(modifier = Modifier.height(8.dp))

        GridContainer(
            cellSize = cellSize,
            viewModel = viewModel,
        )
    }
}

@Composable
fun GridContainer(
    cellSize: Dp,
    viewModel: WordSearchViewModel,
) {
    val cellSizePx = with(LocalDensity.current) { cellSize.toPx() }
    val grid by viewModel.gridState.collectAsState()
    val selectedCells by viewModel.selectedCells.collectAsState()
    val selectedLines by viewModel.selectedLines.collectAsState()
    val currentLineColor by viewModel.currentLineColor.collectAsState()
    val currentLine by viewModel.currentLine.collectAsState()

    Log.d(TAG, "GridContainer render currentLineColor: $currentLineColor")

    Box(
        modifier =
            Modifier
                .size(cellSize * grid[0].size, cellSize * grid.size)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .DrawLines(selectedLines, currentLine)
                .HandleGestures(viewModel, cellSizePx),
        contentAlignment = Alignment.Center,
    ) {
//        DrawLines(selectedLines = selectedLines, currentLineColor = currentLineColor)
        GridDisplay(grid = grid, cellSize = cellSize, selectedCells = selectedCells)
    }
}

@Composable
fun GridDisplay(
    grid: List<List<Char>>,
    cellSize: Dp,
    selectedCells: Set<Pair<Int, Int>>,
) {
    Log.d(TAG, "GridDisplay render")
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
                        modifier = Modifier.size(cellSize),
                        contentAlignment = Alignment.Center,
                    ) {
                        val isSelected =
                            selectedCells.contains(rowIndex to colIndex)
                        Text(
                            text = letter.toString(),
                            color = if (isSelected) Color.White else Color.Black,
                            style =
                                TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.DrawLines(
    selectedLines: List<Line>,
    currentLine: Line?,
): Modifier =
    this.then(
        drawBehind {
            selectedLines.forEach { line ->
                drawLinePath(line.offsets, line.color)
            }
//         Drawing the currently dragged line (if needed)
            currentLine?.let {
                drawLinePath(currentLine.offsets, currentLine.color)
            }
        },
    )

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

@Suppress("ktlint:standard:function-naming")
fun Modifier.HandleGestures(
    viewModel: WordSearchViewModel,
    cellSizePx: Float,
): Modifier =
    this.then(
        pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset -> viewModel.onDragStart(offset, cellSizePx) },
                onDragEnd = { viewModel.onDragEnd(cellSizePx) },
                onDrag = { change, _ -> viewModel.onDrag(change.position, cellSizePx) },
            )
        },
    )

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
) {
    FlowRow(
        modifier =
            modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
    ) {
        words.forEach { word ->
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

    val viewModel = WordSearchViewModel()

    WordGrid(
        viewModel = viewModel,
    )
}
