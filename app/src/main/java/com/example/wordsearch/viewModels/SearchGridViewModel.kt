package com.example.wordsearch.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.repository.Puzzle
import com.example.wordsearch.repository.PuzzleRepository
import com.example.wordsearch.utils.GridUtils.findWordInGrid
import com.example.wordsearch.utils.PuzzleProgressManager
import kotlinx.coroutines.launch
import kotlin.math.abs

class SearchGridViewModel(
    val puzzleRepository: PuzzleRepository,
    private val progressManager: PuzzleProgressManager,
) : ViewModel() {
    @Suppress("ktlint:standard:property-naming")
    private val availableColors =
        listOf(
            Color.Red,
            Color.Blue,
            Color.Green,
            Color.Magenta,
            Color.Cyan,
            Color.Yellow,
            Color(0xFF800080),
            Color(0xFFFFA500),
            Color(0xFF008080),
        )

    // List of words to find
    private val _words = mutableStateListOf<Word>()
    val words: List<Word> = _words

    // Grid state variable
    var grid by mutableStateOf(emptyList<List<Char>>())
        private set

    var selectedCells by mutableStateOf(listOf<Pair<Int, Int>>())
        private set

    var startCell by mutableStateOf<Pair<Int, Int>?>(null)
        private set

    var endCell by mutableStateOf<Pair<Int, Int>?>(null)
        private set

    var currentDragPosition by mutableStateOf<Offset?>(null)
        private set

    private val _foundWords = mutableStateListOf<FoundWord>()
    val foundWords: List<FoundWord> = _foundWords

    var isPuzzleCompleted by mutableStateOf(false)
        private set

    var showCompletionDialog by mutableStateOf(false)
        private set

    private val _positionOfHintWords = mutableStateListOf<Pair<Int, Int>>()
    val positionOfHintWords: List<Pair<Int, Int>> = _positionOfHintWords

    private var currentColorIdx = 0
    private val revealedWordsByHint = mutableListOf<Word>()

    var currentLevel by mutableStateOf(0)
        private set

    var totalLevels by mutableStateOf(0)
        private set

    private var currentPuzzle by mutableStateOf<Puzzle?>(null)
        private set

    init {
        // Load the puzzles when the ViewModel is initialized
        viewModelScope.launch {
            puzzleRepository.loadPuzzles()
            totalLevels = puzzleRepository.getTotalPuzzles()
            // Load the saved progress from SharedPreferences
            currentLevel = progressManager.getCurrentLevel()
            loadPuzzle(currentLevel)
        }
    }

    private fun loadPuzzle(level: Int) {
        currentPuzzle = puzzleRepository.getPuzzleByIndex(level)
        currentPuzzle?.let {
            grid = currentPuzzle!!.grid
            setWords(currentPuzzle!!.words)
        }
    }

    private fun markPuzzleAsSolved() {
        // Increment the level after solving and save the progress
        currentLevel++
        progressManager.saveCurrentLevel(currentLevel)

        if (currentLevel < totalLevels) {
            loadPuzzle(currentLevel)
        }
    }

    fun loadNextPuzzle() {
        showCompletionDialog = false
        markPuzzleAsSolved()
        resetPuzzleState()
    }

    private fun onPuzzleCompleted() {
        showCompletionDialog = true
        isPuzzleCompleted = true
    }

    fun onDismissDialog() {
        showCompletionDialog = false
    }

    private fun setWords(wordList: List<String>) {
        _words.clear()
        _words.addAll(wordList.map { Word(it, false) })
    }

    fun onDragStart(
        offset: Offset,
        cellSize: Float,
    ) {
        startCell =
            Pair(
                (offset.y / cellSize).toInt(),
                (offset.x / cellSize).toInt(),
            )

        endCell = startCell
        currentDragPosition = offset
        selectedCells = listOf(startCell!!)
    }

    fun onDragEnd() {
        val selectedWord = getSelectedWord()

        selectedWord.let {
            val isMatched =
                _words.any {
                    !it.found && (it.text == selectedWord || it.text == selectedWord.reversed())
                }

//            Log.d("SearchGridViewModel", "Selected Word: $selectedWord")
//            Log.d("SearchGridViewModel", "Is Matched: $isMatched")
//            Log.d("SearchGridViewModel", "selectedCells: $selectedCells")

            if (isMatched) {
                onWordFound(selectedWord)
            }
        }

        resetGridStates()
    }

    fun onDrag(offset: Offset) {
        currentDragPosition = offset
    }

    private fun resetGridStates() {
        startCell = null
        endCell = null
        currentDragPosition = null
        selectedCells = emptyList()
    }

    fun resetPuzzleState() {
        currentColorIdx = 0
        isPuzzleCompleted = false
        _foundWords.clear()
        _positionOfHintWords.clear()
        revealedWordsByHint.clear()
        resetGridStates()
    }

    private fun checkIfPuzzleCompleted() {
        if (_words.all { it.found }) {
            onPuzzleCompleted()
        }
    }

    private fun onWordFound(selectedWord: String) {
        markWordAsFound(selectedWord)
        currentColorIdx++
        checkIfPuzzleCompleted()
    }

    fun getCurrentLineColor(): Color = availableColors[currentColorIdx % availableColors.size]

    fun updateSelectedCells(
        constrainedEnd: Offset,
        cellSize: Float,
    ) {
        endCell =
            Pair(
                (constrainedEnd.y / cellSize).toInt(),
                (constrainedEnd.x / cellSize).toInt(),
            )
        selectedCells = getSelectedCells(startCell!!, endCell!!, grid.size, grid.size)
    }

    private fun getSelectedWord(): String = selectedCells.map { grid[it.first][it.second] }.joinToString("")

    private fun markWordAsFound(word: String) {
        val index = _words.indexOfFirst { it.text == word || it.text == word.reversed() }
        if (index != -1) {
            _words[index] = _words[index].copy(found = true)
            _foundWords.add(
                FoundWord(
                    word,
                    getCurrentLineColor(),
                    selectedCells.toList(),
                ),
            )
        }
    }

    fun highlightFirstCharacter() {
        val word =
            _words.firstOrNull { w ->
                !foundWords.any { it.text == w.text } && !revealedWordsByHint.any { it.text == w.text }
            }

        if (word == null) {
            return
        }
        revealedWordsByHint.add(word)
        // Find the cell where the first character is located in the grid
        val position = findWordInGrid(grid = grid, word = word.text)
        position?.let {
            _positionOfHintWords.add(position)
        }
    }

    private fun getSelectedCells(
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        rows: Int,
        cols: Int,
    ): List<Pair<Int, Int>> {
        val dx = end.first - start.first
        val dy = end.second - start.second

        return when {
            dx == 0 ->
                (
                    minOf(start.second, end.second)..maxOf(
                        start.second,
                        end.second,
                    )
                ).map { Pair(start.first, it) }

            dy == 0 ->
                (minOf(start.first, end.first)..maxOf(start.first, end.first)).map {
                    Pair(
                        it,
                        start.second,
                    )
                }

            abs(dx) == abs(dy) -> {
                val xRange = if (dx > 0) start.first..end.first else start.first downTo end.first
                val yRange =
                    if (dy > 0) start.second..end.second else start.second downTo end.second
                xRange.zip(yRange).toList()
            }

            else -> listOf(start)
        }.filter { it.first in 0 until rows && it.second in 0 until cols }
    }

    // inject puzzle repository
    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    // Get the application instance
                    val application = this[APPLICATION_KEY] as WordSearchApplication

                    // Pass the repository and application to HomeViewModel
                    SearchGridViewModel(
                        puzzleRepository = application.container.puzzleRepository,
                        progressManager = application.container.puzzleProgressManager,
                    )
                }
            }
    }
}

data class Word(
    val text: String,
    val found: Boolean,
)

// Data class to store a found word with its corresponding line and color
data class FoundWord(
    val text: String,
    val color: Color,
    val cells: List<Pair<Int, Int>>,
)
