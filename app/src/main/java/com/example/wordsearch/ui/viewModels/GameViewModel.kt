package com.example.wordsearch.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    private val _currentPuzzleId = MutableStateFlow(1)
    val currentPuzzleId: StateFlow<Int> = _currentPuzzleId

    private val _maxPuzzlesPerLevel = MutableStateFlow(2)
    val maxPuzzlesPerLevel: StateFlow<Int> = _maxPuzzlesPerLevel

    private val _isAllPuzzlesCompleted = MutableStateFlow(false)
    val isAllPuzzlesCompleted: StateFlow<Boolean> = _isAllPuzzlesCompleted

    fun initCurrentPuzzleId(puzzleId: Int) {
        _currentPuzzleId.value = puzzleId
    }

    fun initMaxPuzzlesPerLevel(totalPuzzles: Int) {
        _maxPuzzlesPerLevel.value = totalPuzzles
    }

    private fun checkPuzzleCompletion() {
        // Logic to check if the current puzzle is completed
        if (_currentPuzzleId.value > _maxPuzzlesPerLevel.value) {
            onAllPuzzlesCompleted()
        }
    }

    private fun onAllPuzzlesCompleted() {
        _isAllPuzzlesCompleted.value = true
    }

    fun resetGameState() {
        _currentPuzzleId.value = 1
        _isAllPuzzlesCompleted.value = false
    }

    fun onNextPuzzle() {
        // Logic to move to the next puzzle
        _currentPuzzleId.value += 1
        checkPuzzleCompletion()
    }
}
