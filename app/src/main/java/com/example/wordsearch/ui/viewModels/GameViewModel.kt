package com.example.wordsearch.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {
    private val _currentPuzzlePartIndex = MutableStateFlow(0)
    val currentPuzzlePartIndex: StateFlow<Int> = _currentPuzzlePartIndex

    private val _maxPuzzleParts = MutableStateFlow(0)
    val maxPuzzleParts: StateFlow<Int> = _maxPuzzleParts

    private val _isAllPuzzlePartsCompleted = MutableStateFlow(false)
    val isAllPuzzlePartsCompleted: StateFlow<Boolean> = _isAllPuzzlePartsCompleted

    fun initCurrentPuzzleId(puzzleId: Int) {
        _currentPuzzlePartIndex.value = puzzleId
    }

    private fun checkPuzzleCompletion() {
        // Logic to check if the current puzzle is completed
        if (_currentPuzzlePartIndex.value >= _maxPuzzleParts.value) {
            onAllPuzzlePartsCompleted()
        }
    }

    private fun onAllPuzzlePartsCompleted() {
        _isAllPuzzlePartsCompleted.value = true
    }

    fun resetGameState() {
        _isAllPuzzlePartsCompleted.value = false
    }

    fun onNextPuzzlePart() {
        // Logic to move to the next puzzle
        _currentPuzzlePartIndex.value += 1
        checkPuzzleCompletion()
    }

    fun initMaxPuzzleParts(size: Int) {
        _maxPuzzleParts.value = size
    }
}
