package com.example.wordsearch.viewModels

import android.app.Application
import android.media.SoundPool
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.R
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
    application: Application,
) : AndroidViewModel(application) {
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

    private val _uiState = mutableStateOf(SearchGridState())
    val uiState: State<SearchGridState> = _uiState

    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(1).build()
    private var cellEnterSound: Int = 0
    private var wordMatchSound: Int = 0
    private var lastPlayedCell: Pair<Int, Int>? = null
    private var lastSoundPlayedTime = 0L
    private val soundPlayInterval = 100L

    init {
        cellEnterSound = soundPool.load(getApplication(), R.raw.click, 1)
        wordMatchSound = soundPool.load(getApplication(), R.raw.crank, 1)

        // Load the puzzles when the ViewModel is initialized
        viewModelScope.launch {
            puzzleRepository.loadPuzzles()
            _uiState.value = _uiState.value.copy(totalLevels = puzzleRepository.getTotalPuzzles())
            // Load the saved progress from SharedPreferences
            _uiState.value = _uiState.value.copy(currentLevel = progressManager.getCurrentLevel())
            loadPuzzle(_uiState.value.currentLevel)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundPool.release() // Release resources
    }

    private fun loadPuzzle(level: Int) {
        val puzzle = puzzleRepository.getPuzzleByIndex(level)
        puzzle?.let {
            _uiState.value =
                _uiState.value.copy(
                    grid = puzzle.grid,
                    currentPuzzle = puzzle,
                )
            setWords(puzzle.words)
        }
    }

    private fun markPuzzleAsSolved() {
        // Increment the level after solving and save the progress
        val nextLevel = _uiState.value.currentLevel + 1
        _uiState.value = _uiState.value.copy(currentLevel = nextLevel)
        progressManager.saveCurrentLevel(nextLevel)

        if (nextLevel < _uiState.value.totalLevels) {
            loadPuzzle(nextLevel)
        }
    }

    fun loadNextPuzzle() {
        _uiState.value = _uiState.value.copy(showCompletionDialog = false)
        markPuzzleAsSolved()
        resetPuzzleState()
    }

    private fun onPuzzleCompleted() {
        _uiState.value = _uiState.value.copy(showCompletionDialog = true, isPuzzleCompleted = true)
    }

    fun onDismissDialog() {
        _uiState.value = _uiState.value.copy(showCompletionDialog = false)
    }

    private fun setWords(wordList: List<String>) {
        _uiState.value =
            _uiState.value.copy(
                words = wordList.map { Word(it, false) },
            )
    }

    fun onDragStart(
        offset: Offset,
        cellSize: Float,
    ) {
        val startCell =
            Pair(
                (offset.y / cellSize).toInt(),
                (offset.x / cellSize).toInt(),
            )

        _uiState.value =
            _uiState.value.copy(
                startCell = startCell,
                endCell = startCell,
                currentDragPosition = offset,
                selectedCells = listOf(startCell),
            )

        lastPlayedCell = startCell
        playCellEnterSound()
    }

    fun onDragEnd() {
        // Reset lastPlayedCells when drag ends
        lastPlayedCell = null
        if (!isWordMatched()) {
            resetGridStates()
        }
    }

    fun onDrag(offset: Offset) {
        _uiState.value = _uiState.value.copy(currentDragPosition = offset)
    }

    private fun playCellEnterSound() {
        soundPool.play(cellEnterSound, 1f, 1f, 0, 0, 2f)
    }

    private fun playWordMatchedSound() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSoundPlayedTime >= soundPlayInterval) {
            soundPool.play(wordMatchSound, 1f, 1f, 0, 0, 1f)
            lastSoundPlayedTime = currentTime
        }
    }

    private fun isWordMatched(): Boolean {
        val selectedWord = getSelectedWord()
        val isMatched =
            _uiState.value.words.any {
                !it.found && (it.text == selectedWord || it.text == selectedWord.reversed())
            }
        return isMatched
    }

    private fun checkIfWordFound() {
        val selectedWord = getSelectedWord()
        if (isWordMatched()) {
            playWordMatchedSound()
            onWordFound(selectedWord)
            resetGridStates()
        }
    }

    private fun resetGridStates() {
        _uiState.value =
            _uiState.value.copy(
                startCell = null,
                endCell = null,
                currentDragPosition = null,
                selectedCells = emptyList(),
            )
    }

    // Update selected cells and check if a word is found
    fun updateSelectedCells(
        constrainedEnd: Offset,
        cellSize: Float,
    ) {
        val endCell =
            Pair(
                (constrainedEnd.y / cellSize).toInt(),
                (constrainedEnd.x / cellSize).toInt(),
            )
        val selectedCells =
            getSelectedCells(
                _uiState.value.startCell!!,
                endCell,
                rows = _uiState.value.grid.size,
                cols = _uiState.value.grid.size,
            )

        _uiState.value =
            _uiState.value.copy(
                endCell = endCell,
                selectedCells = selectedCells,
            )

        // Play sound if we've entered a new cell
        if (endCell != lastPlayedCell) {
            lastPlayedCell = endCell
            playCellEnterSound()
            // Check if the word is found
            checkIfWordFound()
        }
    }

    fun resetPuzzleState() {
        _uiState.value =
            _uiState.value.copy(
                currentColorIdx = 0,
                isPuzzleCompleted = false,
                foundWords = emptyList(),
                positionOfHintWords = emptyList(),
                revealedWordsByHint = emptyList(),
            )
        resetGridStates()
    }

    private fun getSelectedWord(): String =
        _uiState.value.selectedCells
            .map { _uiState.value.grid[it.first][it.second] }
            .joinToString("")

    private fun markWordAsFound(word: String) {
        val words = _uiState.value.words.toMutableList()
        val index =
            _uiState.value.words.indexOfFirst { it.text == word || it.text == word.reversed() }
        if (index != -1) {
            words[index] = words[index].copy(found = true)
            _uiState.value =
                _uiState.value.copy(
                    words = words,
                    foundWords =
                        _uiState.value.foundWords +
                            FoundWord(
                                word,
                                getCurrentLineColor(),
                                _uiState.value.selectedCells.toList(),
                            ),
                )
        }
    }

    private fun onWordFound(selectedWord: String) {
        markWordAsFound(selectedWord)
        checkIfPuzzleCompleted()
        _uiState.value =
            _uiState.value.copy(
                currentColorIdx = _uiState.value.currentColorIdx + 1,
            )
    }

    fun getCurrentLineColor(): Color {
        val currentColorIdx = _uiState.value.currentColorIdx
        return availableColors[currentColorIdx % availableColors.size]
    }

    private fun checkIfPuzzleCompleted() {
        if (_uiState.value.words.all { it.found }) {
            onPuzzleCompleted()
        }
    }

    fun highlightFirstCharacter() {
        val word =
            _uiState.value.words.firstOrNull { w ->
                !_uiState.value.foundWords.any { it.text == w.text } &&
                    !_uiState.value.revealedWordsByHint.any { it.text == w.text }
            }

        word?.let { selectedWord ->
            val position = findWordInGrid(grid = _uiState.value.grid, word = selectedWord.text)
            position?.let {
                _uiState.value =
                    _uiState.value.copy(
                        revealedWordsByHint = _uiState.value.revealedWordsByHint + selectedWord,
                        positionOfHintWords = _uiState.value.positionOfHintWords + position,
                    )
            }
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
                        application,
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

data class SearchGridState(
    val grid: List<List<Char>> = emptyList(),
    val selectedCells: List<Pair<Int, Int>> = emptyList(),
    val startCell: Pair<Int, Int>? = null,
    val endCell: Pair<Int, Int>? = null,
    val currentDragPosition: Offset? = null,
    val foundWords: List<FoundWord> = emptyList(),
    val isPuzzleCompleted: Boolean = false,
    val showCompletionDialog: Boolean = false,
    val positionOfHintWords: List<Pair<Int, Int>> = emptyList(),
    val currentLevel: Int = 0,
    val totalLevels: Int = 0,
    val currentPuzzle: Puzzle? = null,
    val words: List<Word> = emptyList(), // List of words to find
    val currentColorIdx: Int = 0, // Current color index for lines
    val revealedWordsByHint: List<Word> = emptyList(), // Words revealed by hints
)
