package com.example.wordsearch.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.repository.PuzzleProgressRepository

class HomeViewModel(
    val puzzleProgressRepository: PuzzleProgressRepository,
) : ViewModel() {
    fun getPuzzleProgress(puzzleId: Int) = puzzleProgressRepository.getPuzzleProgress(puzzleId)

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val puzzleProgressRepository =
                        (this[APPLICATION_KEY] as WordSearchApplication).container.puzzleProgressRepository
                    HomeViewModel(
                        puzzleProgressRepository,
                    )
                }
            }
    }
}
