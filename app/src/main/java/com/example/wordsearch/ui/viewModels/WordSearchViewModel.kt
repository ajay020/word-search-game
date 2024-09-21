package com.example.wordsearch.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.wordsearch.utils.GridUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WordSearchViewModel : ViewModel() {
    private val _gridState = MutableStateFlow(generateGrid())
    val gridState: StateFlow<List<List<Char>>> = _gridState

    private val _wordListState =
        MutableStateFlow(listOf("cat", "mat", "bat", "rat", "sat")) // Example words
    val wordListState: StateFlow<List<String>> = _wordListState

    private fun generateGrid(): List<List<Char>> {
        // Your logic to generate a grid with random letters and place words
        val wordList = listOf("cat", "mat", "bat", "rat", "sat")
        return GridUtils.generateGrid(wordList)
    }
}
