package com.example.wordsearch.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchGameViewModal : ViewModel() {
    private val _uiState = MutableStateFlow(SearchGameState())
    val uiState: StateFlow<SearchGameState> = _uiState.asStateFlow()

    // Function to use a hint
    fun useHint() {
        if (uiState.value.coins > 0) {
            _uiState.update { currentState ->
                currentState.copy(
                    coins = currentState.coins - 10,
                    availableHints = currentState.availableHints - 1,
                )
            }
        } else {
            // Handle no more coins available, e.g., show a message
        }
    }
}

data class SearchGameState(
    val coins: Int = 50,
    val availableHints: Int = 6,
)
