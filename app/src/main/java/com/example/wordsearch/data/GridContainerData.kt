package com.example.wordsearch.data

import androidx.compose.ui.unit.Dp

data class GridContainerData(
    val cellSize: Dp,
    val grid: List<List<Char>>,
    val selectedLines: List<Line>,
    val currentLine: Line?,
    val selectedCells: Set<Pair<Int, Int>>,
)
