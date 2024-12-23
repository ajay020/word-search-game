package com.example.wordsearch.viewModels

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.wordsearch.data.Line
import com.example.wordsearch.data.colors
import com.example.wordsearch.utils.GridUtils.calculateSelectedCells
import com.example.wordsearch.utils.GridUtils.findWordInGrid
import com.example.wordsearch.utils.GridUtils.getCellCenter
import com.example.wordsearch.utils.GridUtils.isStraightLine
import com.example.wordsearch.utils.GridUtils.offsetToGridCoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val TAG = "WordViewModel"

class WordGridViewModel : ViewModel() {
    private val availableColors = colors

    private val _gridState = MutableStateFlow<List<List<Char>>>(emptyList())
    val gridState: StateFlow<List<List<Char>>> = _gridState

    private val _positionOfHintWords = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val positionOfHintWords: StateFlow<List<Pair<Int, Int>>> = _positionOfHintWords

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
    private var cellSizePx: Float = 0f
    private var revealedWordsByHint = mutableListOf<String>()

    private val hitboxTolerance =
        cellSizePx / 2 // You can adjust this value for more or less tolerance

    fun updateCellSizePx(newSizePx: Float) {
        cellSizePx = newSizePx
    }

    fun initGrid(
        words: List<String>,
        grid: List<List<Char>>,
    ) {
        _gridState.value = grid
        _wordListState.value = words
    }

    fun onDragStart(offset: Offset) {
        val (row, col) =
            offsetToGridCoordinate(
                offset,
                cellSizePx,
                rowSize = _gridState.value.size,
                colSize = _gridState.value[0].size,
                hitboxTolerance,
            )
        startCell = row to col
        _selectedCells.value = setOf(row to col)

        val line =
            Line(offsets = listOf(getCellCenter(row, col, cellSizePx)), color = getNextColor())
        _currentLine.value = line
    }

    fun onDrag(offset: Offset) {
        val (row, col) =
            offsetToGridCoordinate(
                offset,
                cellSizePx,
                rowSize = _gridState.value.size,
                colSize = _gridState.value[0].size,
                hitboxTolerance,
            )
        val newCell = row to col

        if (isStraightLine(startCell!!, newCell)) {
            _selectedCells.value =
                calculateSelectedCells(
                    start = startCell!!,
                    end = newCell,
                    columnSize = _gridState.value[0].size,
                    rowSize = _gridState.value.size,
                )

            _currentWord.value =
                _selectedCells.value.map { (r, c) -> _gridState.value[r][c] }.joinToString("")

            // Gather the centers of all selected cells
            val selectedCenters =
                _selectedCells.value.map { (r, c) ->
                    getCellCenter(r, c, cellSizePx)
                }

            // Instead of interpolating for all points at once, we just pass the cell centers
            _currentLine.value =
                _currentLine.value?.copy(
                    offsets = selectedCenters,
                )
        }
    }

    fun onDragEnd() {
        startCell?.let {
            val selectedWord =
                _selectedCells.value.map { (r, c) -> _gridState.value[r][c] }.joinToString("")

            if (_wordListState.value.contains(selectedWord) ||
                wordListState.value.contains(selectedWord.reversed()) &&
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

        // reset hints
        revealedWordsByHint.clear()
        _positionOfHintWords.value = emptyList()
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

    fun highlightFirstCharacter() {
        val word =
            _wordListState.value.firstOrNull {
                !_foundWords.value.contains(it) &&
                    !revealedWordsByHint.contains(it)
            }
        if (word == null) {
            return
        }
        revealedWordsByHint.add(word)
        // Find the cell where the first character is located in the grid
        val position = findWordInGrid(grid = _gridState.value, word = word)
        position?.let {
            _positionOfHintWords.value += it
        }
    }
}
