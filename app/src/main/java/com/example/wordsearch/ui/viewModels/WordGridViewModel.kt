package com.example.wordsearch.ui.viewModels

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.wordsearch.data.Line
import com.example.wordsearch.data.colors
import com.example.wordsearch.utils.GridUtils.calculateSelectedCells
import com.example.wordsearch.utils.GridUtils.generateGrid
import com.example.wordsearch.utils.GridUtils.getCellCenter
import com.example.wordsearch.utils.GridUtils.interpolatePoints
import com.example.wordsearch.utils.GridUtils.isStraightLine
import com.example.wordsearch.utils.GridUtils.offsetToGridCoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val TAG = "WordViewModel"

class WordGridViewModel : ViewModel() {
    private val availableColors = colors
    private val interpolationSteps = 10

    private val _gridState = MutableStateFlow<List<List<Char>>>(emptyList())
    val gridState: StateFlow<List<List<Char>>> = _gridState

    private val _wordListState = MutableStateFlow<List<String>>(emptyList())
    val wordListState: StateFlow<List<String>> = _wordListState

    private val _selectedCells = MutableStateFlow(setOf<Pair<Int, Int>>())
    val selectedCells: StateFlow<Set<Pair<Int, Int>>> = _selectedCells

    private val _foundWords = MutableStateFlow(setOf<String>())
    val foundWords: StateFlow<Set<String>> = _foundWords

    private val _selectedLines = MutableStateFlow(emptyList<Line>())
    val selectedLines: StateFlow<List<Line>> = _selectedLines

    private val _currentWord = MutableStateFlow("")
    val currentWord: StateFlow<String> = _currentWord

    private val _currentLine = MutableStateFlow<Line?>(null)
    val currentLine: StateFlow<Line?> = _currentLine

    private val _isGameCompleted = MutableStateFlow(false)
    val isGameCompleted: StateFlow<Boolean> = _isGameCompleted

    private var startCell: Pair<Int, Int>? = null
    private var currentColorIndex = 0

    fun initGrid(words: List<String>) {
        _gridState.value = generateGrid(words)
        _wordListState.value = words
    }

    fun onDragStart(
        offset: Offset,
        cellSize: Float,
    ) {
        val (row, col) = offsetToGridCoordinate(offset, cellSize, _gridState.value.size)
        startCell = row to col
        Log.d(TAG, "DragStart:  $startCell")

        _selectedCells.value = setOf(row to col)

        val line = Line(offsets = listOf(getCellCenter(row, col, cellSize)), color = getNextColor())
        _currentLine.value = line
    }

    fun onDragEnd() {
        startCell?.let {
            val selectedWord =
                _selectedCells.value.map { (r, c) -> _gridState.value[r][c] }.joinToString("")

            if (_wordListState.value.contains(selectedWord) &&
                !_foundWords.value.contains(
                    selectedWord,
                )
            ) {
                // Add the word to the found words set
                onWordFound(selectedWord)

                if (_currentLine.value != null) {
                    _selectedLines.value += _currentLine.value!!
                }
                // select next color
                currentColorIndex++
            }
        }
        resetDragState()
    }

    fun onDrag(
        offset: Offset,
        cellSize: Float,
    ) {
        val (row, col) = offsetToGridCoordinate(offset, cellSize, _gridState.value.size)
        val newCell = row to col

        if (isStraightLine(startCell!!, newCell)) {
            _selectedCells.value =
                calculateSelectedCells(startCell!!, newCell, _gridState.value.size)

            _currentWord.value =
                _selectedCells.value.map { (r, c) -> _gridState.value[r][c] }.joinToString("")

            val startOffset =
                getCellCenter(
                    startCell!!.first,
                    startCell!!.second,
                    cellSize,
                )
            val endOffset =
                getCellCenter(row, col, cellSize)

            _currentLine.value =
                _currentLine.value?.copy(
                    offsets =
                        interpolatePoints(
                            startOffset,
                            endOffset,
                            interpolationSteps,
                        ),
                )
        }
    }

    // Check if all words are found and mark the game as completed
    private fun checkGameCompletion() {
        if (_foundWords.value.size == _wordListState.value.size) {
            _isGameCompleted.value = true
        }
    }

    fun resetGameState() {
        _isGameCompleted.value = false
        _foundWords.value = emptySet() // Reset found words for next game
        _selectedCells.value = emptySet()
        _selectedLines.value = emptyList()
        _currentWord.value = ""
        _currentLine.value = null
    }

    private fun onWordFound(word: String) {
        _foundWords.value += word
        checkGameCompletion()
    }

    private fun resetDragState() {
        startCell = null
        _selectedCells.value = setOf()
        _currentWord.value = ""
        _currentLine.value = null
    }

    private fun getNextColor(): Color {
        currentColorIndex %= availableColors.size
        return availableColors[currentColorIndex]
    }
}
