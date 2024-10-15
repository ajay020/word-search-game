package com.example.wordsearch.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlin.math.abs

class SearchGridViewModel : ViewModel() {
    // List of words to find
    private val _words = mutableStateListOf<Word>()
    val words: List<Word> = _words

    // Grid state variable
    var grid by mutableStateOf(emptyList<List<Char>>())
        private set

    // Rows and columns are based on the grid size
    val rows: Int
        get() = grid.size

    val cols: Int
        get() = grid.firstOrNull()?.size ?: 0

    var selectedCells by mutableStateOf(listOf<Pair<Int, Int>>())
        private set

    var startCell by mutableStateOf<Pair<Int, Int>?>(null)
        private set

    var endCell by mutableStateOf<Pair<Int, Int>?>(null)
        private set

    var currentDragPosition by mutableStateOf<Offset?>(null)
        private set

    fun setWords(wordList: List<String>) {
        _words.clear()
        _words.addAll(wordList.map { Word(it, false) })
    }

    fun initGrid(newGrid: List<List<Char>>) {
        grid = newGrid
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
        if (selectedWord != null) {
            markWordAsFound(selectedWord)
        }

        startCell = null
        endCell = null
        currentDragPosition = null
        selectedCells = emptyList()
    }

    fun onDrag(offset: Offset) {
        currentDragPosition = offset
    }

    fun updateSelectedCells(
        constrainedEnd: Offset,
        cellSize: Float,
    ) {
        endCell =
            Pair(
                (constrainedEnd.y / cellSize).toInt(),
                (constrainedEnd.x / cellSize).toInt(),
            )
        selectedCells = getSelectedCells(startCell!!, endCell!!, rows, cols)
    }

    private fun getSelectedWord(): String? = selectedCells.map { grid[it.first][it.second] }.joinToString("")

    private fun markWordAsFound(word: String) {
        val index = _words.indexOfFirst { it.text.equals(word, ignoreCase = true) }
        if (index != -1) {
            _words[index] = _words[index].copy(found = true)
        }
    }

    private fun generateGrid(
        rows: Int,
        cols: Int,
    ): List<List<Char>> = List(rows) { List(cols) { ('A'..'Z').random() } }

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
}

data class Word(
    val text: String,
    val found: Boolean,
)
