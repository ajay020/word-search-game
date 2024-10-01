package com.example.wordsearch.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
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
        gridSize: Int,
    ): Pair<Int, Int> {
        val col = (offset.x / cellSizePx).toInt().coerceIn(0, gridSize - 1)
        val row = (offset.y / cellSizePx).toInt().coerceIn(0, gridSize - 1)

        return row to col
    }

    // Function to generate a list of interpolated points between two offsets
    fun interpolatePoints(
        start: Offset,
        end: Offset,
        steps: Int,
    ): List<Offset> =
        List(steps) { step ->
            lerp(start, end, step.toFloat() / (steps - 1))
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

    fun interpolateCells(
        start: Pair<Int, Int>,
        end: Pair<Int, Int>,
    ): List<Pair<Int, Int>> {
        val cells = mutableListOf<Pair<Int, Int>>()

        var x1 = start.second
        var y1 = start.first
        val x2 = end.second
        val y2 = end.first

        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1
        var err = dx - dy

        while (true) {
            cells.add(y1 to x1) // Add current cell
            if (x1 == x2 && y1 == y2) break
            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x1 += sx
            }
            if (e2 < dx) {
                err += dx
                y1 += sy
            }
        }

        return cells
    }

    // Function to calculate the center of a grid cell based on row, column, and cell size
    fun getCellCenter(
        row: Int,
        col: Int,
        cellSize: Float,
    ): Offset {
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
}
