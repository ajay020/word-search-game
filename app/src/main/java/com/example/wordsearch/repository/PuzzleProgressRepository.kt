package com.example.wordsearch.repository

import android.content.Context
import androidx.core.content.edit
import com.example.wordsearch.data.PuzzleProgress

class PuzzleProgressRepository(
    private val context: Context,
) {
    private val sharedPreferences =
        context.getSharedPreferences("puzzle_progress", Context.MODE_PRIVATE)

    // Save progress of a puzzle
    fun savePuzzleProgress(
        puzzleId: Int,
        completedParts: Int,
        totalParts: Int,
    ) {
        sharedPreferences.edit {
            putInt("${puzzleId}_total", totalParts)
            putInt("${puzzleId}_completed", completedParts)
        }
    }

    // Retrieve progress of a puzzle (total and completed parts)
    fun getPuzzleProgress(puzzleId: Int): PuzzleProgress? {
        val totalParts = sharedPreferences.getInt("${puzzleId}_total", -1)
        val completedParts = sharedPreferences.getInt("${puzzleId}_completed", -1)

        return if (totalParts != -1 && completedParts != -1) {
            PuzzleProgress(puzzleId = puzzleId, completedParts = completedParts, totalParts = totalParts)
        } else {
            null
        }
    }

    // Update completed parts for a puzzle
    fun updateCompletedParts(
        puzzleId: Int,
        newCompletedParts: Int,
    ) {
        val totalParts = sharedPreferences.getInt("${puzzleId}_total", -1)
        if (totalParts != -1) {
            sharedPreferences.edit {
                putInt("${puzzleId}_completed", newCompletedParts)
            }
        }
    }

    // Clear progress for a puzzle (reset)
    fun clearPuzzleProgress(puzzleId: Int) {
        sharedPreferences.edit {
            remove("${puzzleId}_total")
            remove("${puzzleId}_completed")
        }
    }

    // Clear all puzzle progress
    fun clearAllProgress() {
        sharedPreferences.edit {
            clear()
        }
    }
}
