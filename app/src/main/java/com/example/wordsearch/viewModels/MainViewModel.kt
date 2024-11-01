package com.example.wordsearch.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.R
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.utils.PuzzleProgressManager
import com.example.wordsearch.utils.ThemePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val themePreferenceManager: ThemePreferenceManager,
    private val puzzleProgressManager: PuzzleProgressManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        _uiState.value =
            MainUiState(
                totalWordsFound = puzzleProgressManager.getTotalWordFound(),
                backGroundImage = themePreferenceManager.getBackgroundImage(),
            )
    }

    fun updateBackgroundImage(image: Int) {
        _uiState.value = _uiState.value.copy(backGroundImage = image)
        themePreferenceManager.saveBackgroundImage(image)
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as WordSearchApplication)
                    val themePreferenceManager = application.container.themePreferenceManager
                    val puzzleProgressManager = application.container.puzzleProgressManager
                    MainViewModel(
                        themePreferenceManager,
                        puzzleProgressManager,
                    )
                }
            }
    }
}

data class MainUiState(
    val backGroundImage: Int = R.drawable.sky,
    val totalWordsFound: Int = 0,
)
