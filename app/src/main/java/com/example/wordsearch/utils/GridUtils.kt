package com.example.wordsearch.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

// Directions for word placement (horizontal, vertical, diagonal)
enum class Direction {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL,
}

object GridUtils {
    // Minimum grid size
    private const val MIN_GRID_SIZE = 5
    // Define a tolerance value that extends the hitbox around the cell center

    // Generate a random grid with words placed
    fun generateGrid(wordList: List<String>): List<MutableList<Char>> {
        // Determine the grid size based on the longest word
        val longestWordLength = wordList.maxOf { it.length }
        val gridSize = maxOf(longestWordLength, MIN_GRID_SIZE)

        // Initialize the grid with empty characters
        val grid = MutableList(gridSize + 1) { MutableList(gridSize) { ' ' } }

        // Place each word in the grid
        wordList.forEach { word ->
            placeWordInGrid(grid, word)
        }

        // Fill remaining empty spaces with random letters
        fillEmptySpaces(grid)

        return grid
    }

    // Function to place a word in the grid
    // Function to place a word in the grid
    private fun placeWordInGrid(
        grid: MutableList<MutableList<Char>>,
        word: String,
    ) {
        var placed = false
        while (!placed) {
            // Randomly pick a starting point and direction
            val startRow = Random.nextInt(grid.size)
            val startCol = Random.nextInt(grid[0].size)
            val direction = Direction.entries.random()

            // Try to place the word in the chosen direction
            if (canPlaceWord(grid, word, startRow, startCol, direction)) {
                placeWordAt(grid, word, startRow, startCol, direction)
                placed = true
            }
        }
    }

    // Check if the word can be placed at the given position and direction
    // Check if the word can be placed at the given position and direction
    private fun canPlaceWord(
        grid: List<List<Char>>,
        word: String,
        startRow: Int,
        startCol: Int,
        direction: Direction,
    ): Boolean {
        val length = word.length
        return when (direction) {
            Direction.HORIZONTAL -> {
                if (startCol + length > grid[0].size) return false
                for (i in 0 until length) {
                    if (grid[startRow][startCol + i] != ' ' && grid[startRow][startCol + i] != word[i]) {
                        return false
                    }
                }
                true
            }

            Direction.VERTICAL -> {
                if (startRow + length > grid.size) return false
                for (i in 0 until length) {
                    if (grid[startRow + i][startCol] != ' ' && grid[startRow + i][startCol] != word[i]) {
                        return false
                    }
                }
                true
            }

            Direction.DIAGONAL -> {
                if (startRow + length > grid.size || startCol + length > grid[0].size) return false
                for (i in 0 until length) {
                    if (grid[startRow + i][startCol + i] != ' ' && grid[startRow + i][startCol + i] != word[i]) {
                        return false
                    }
                }
                true
            }
        }
    }

    // Place the word in the grid at the given position and direction
    private fun placeWordAt(
        grid: MutableList<MutableList<Char>>,
        word: String,
        startRow: Int,
        startCol: Int,
        direction: Direction,
    ) {
        when (direction) {
            Direction.HORIZONTAL -> {
                for (i in word.indices) {
                    grid[startRow][startCol + i] = word[i]
                }
            }

            Direction.VERTICAL -> {
                for (i in word.indices) {
                    grid[startRow + i][startCol] = word[i]
                }
            }

            Direction.DIAGONAL -> {
                for (i in word.indices) {
                    grid[startRow + i][startCol + i] = word[i]
                }
            }
        }
    }

    // Fill remaining empty spaces in the grid with random letters
    // Function to fill empty spaces with random letters
    private fun fillEmptySpaces(grid: MutableList<MutableList<Char>>) {
        val alphabet = ('A'..'Z').toList()
        for (row in grid) {
            for (i in row.indices) {
                if (row[i] == ' ') {
                    row[i] = alphabet.random()
                }
            }
        }
    }

    fun offsetToGridCoordinate(
        offset: Offset,
        cellSizePx: Float,
        rowSize: Int,
        colSize: Int,
        hitboxTolerance: Float = 0f,
    ): Pair<Int, Int> {
        // Calculate the row and column based on the offset
        val row = ((offset.y + hitboxTolerance) / cellSizePx).toInt().coerceIn(0, rowSize - 1)
        val col = ((offset.x + hitboxTolerance) / cellSizePx).toInt().coerceIn(0, colSize - 1)
//        Log.d("WordGrid", "offset: $offset cellSizePx: $cellSizePx r: $row c: $col")

        return row to col
    }

   fun interpolatePoints(start: Offset, end: Offset, steps: Int): List<Offset> {
        val stepX = (end.x - start.x) / steps
        val stepY = (end.y - start.y) / steps
        return List(steps) { i -> Offset(start.x + stepX * i, start.y + stepY * i) }
    }

    fun calculateSelectedCells(
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
        rowSize: Int,
        columnSize: Int,
    ): Set<Pair<Int, Int>> {
        val selectedCells = mutableSetOf<Pair<Int, Int>>()
        val (startRow, startCol) = start
        val (endRow, endCol) = end

        val rowDiff = endRow - startRow
        val colDiff = endCol - startCol
        val steps = max(abs(rowDiff), abs(colDiff))

        if (steps == 0) return setOf(start)

        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val row = (startRow + t * rowDiff).toInt()
            val col = (startCol + t * colDiff).toInt()
            if (row in 0 until rowSize && col in 0 until columnSize) {
                selectedCells.add(Pair(row, col))
            }
        }

        return selectedCells
    }

    // Function to calculate the center of a grid cell based on row, column, and cell size
    fun getCellCenter(
        row: Int,
        col: Int,
        cellSize: Float,
    ): Offset {
        // Calculate the exact center of a cell, ensuring the line snaps to the center
        val x = col * cellSize + (cellSize / 2)
        val y = row * cellSize + (cellSize / 2)
        return Offset(x, y)
    }

    // Function to check the direction of the drag (row, column, or diagonal)
    fun isStraightLine(
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
    ): Boolean {
        val (startRow, startCol) = start
        val (endRow, endCol) = end

        // Check if the drag is along a row, column, or diagonal
        return (startRow == endRow) ||
            (startCol == endCol) ||
            (
                Math.abs(startRow - endRow) ==
                    Math.abs(
                        startCol - endCol,
                    )
            )
    }

    // Hint
    fun findWordInGrid(
        grid: List<List<Char>>,
        word: String,
    ): Pair<Int, Int>? {
        val directions =
            listOf(
                Pair(0, 1), // Right (horizontal)
                Pair(0, -1), // Left (horizontal)
                Pair(1, 0), // Down (vertical)
                Pair(-1, 0), // Up (vertical)
                Pair(1, 1), // Diagonal right-down
                Pair(1, -1), // Diagonal left-down
                Pair(-1, 1), // Diagonal right-up
                Pair(-1, -1), // Diagonal left-up
            )

        for (row in grid.indices) {
            for (col in grid[row].indices) {
                if (grid[row][col] == word[0]) {
                    val cellCoordinate = Pair(row, col)
                    // First character matches, check if the full word fits in any direction
                    for ((dRow, dCol) in directions) {
                        if (checkWordInDirection(grid, word, cellCoordinate, dRow, dCol)) {
                            // Return the starting position of the word
                            return Pair(row, col)
                        }
                    }
                }
            }
        }
        // Word not found
        return null
    }

    private fun checkWordInDirection(
        grid: List<List<Char>>,
        word: String,
        cellCoordinate: Pair<Int, Int>,
        dRow: Int,
        dCol: Int,
    ): Boolean {
        var currentRow = cellCoordinate.first
        var currentCol = cellCoordinate.second

        for (char in word) {
            if (currentRow !in grid.indices ||
                currentCol !in grid[currentRow].indices ||
                grid[currentRow][currentCol] != char
            ) {
                return false
            }
            // Move in the specified direction
            currentRow += dRow
            currentCol += dCol
        }
        return true
    }
}
