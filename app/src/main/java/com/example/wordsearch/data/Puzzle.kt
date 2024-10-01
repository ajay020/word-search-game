package com.example.wordsearch.data

data class Puzzle(
    val id: Int,
    val parts: List<PuzzlePart>
)

data class PuzzlePart(
    val partId: Int,
    val words: List<String>,
    val isCompleted: Boolean = false
)
