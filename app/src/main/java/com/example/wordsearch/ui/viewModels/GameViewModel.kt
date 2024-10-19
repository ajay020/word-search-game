package com.example.wordsearch.ui.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.repository.PuzzleProgressRepository
import com.example.wordsearch.utils.GridUtils.generateGrid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class GameViewModel(
    private val puzzleProgressRepository: PuzzleProgressRepository,
) : ViewModel() {
    private val _grid = MutableStateFlow<List<List<Char>>>(emptyList())
    val grid: StateFlow<List<List<Char>>> = _grid
    private val _currentPuzzlePartIndex = MutableStateFlow(0)
    val currentPuzzlePartIndex: StateFlow<Int> = _currentPuzzlePartIndex
    private val _coins = MutableStateFlow(30)
    val coins: StateFlow<Int> = _coins
    private val _availableHint = MutableStateFlow(3)
    val availableHint: StateFlow<Int> = _availableHint

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    fun initGrid(words: List<String>) {
        Log.d("GameViewModel", "init grid")
        generateGridAsync(words)
    }

    private fun generateGridAsync(wordList: List<String>) {
        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            val executionTime =
                measureTimeMillis {
                    _grid.value = generateGrid(wordList)
                }
            Log.d("GameViewModel", "Grid generation time: $executionTime ms")
            withContext(Dispatchers.Main) {
                _isLoading.value = false
                // ...
            }
        }
    }

    fun initCurrentPuzzlePartIndex(puzzleId: Int) {
        val puzzleProgress = puzzleProgressRepository.getPuzzleProgress(puzzleId)
        if (puzzleProgress != null) {
            _currentPuzzlePartIndex.value = puzzleProgress.completedParts
        }
    }

    fun savePuzzleProgress(
        puzzleId: Int,
        completedParts: Int,
        totalParts: Int,
    ) {
        Log.d("GameViewModel", "Saving puzzle progress: $puzzleId, $totalParts, $completedParts")

        puzzleProgressRepository.savePuzzleProgress(
            puzzleId = puzzleId,
            completedParts = completedParts,
            totalParts = totalParts,
        )
    }

    fun onNextPuzzlePart() {
        // Logic to move to the next puzzle
        _currentPuzzlePartIndex.value += 1
    }

    // Function to use a hint
    fun useHint() {
        if (_coins.value > 0) {
            _coins.value -= 10 // Deduct 1 coin per hint
            _availableHint.value -= 1
        } else {
            // Handle no more coins available, e.g., show a message
        }
    }

    // Factory to create the ViewModel
    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as WordSearchApplication)
                    val puzzleProgressRepository = application.container.puzzleProgressRepository
                    GameViewModel(puzzleProgressRepository)
                }
            }
    }
}
