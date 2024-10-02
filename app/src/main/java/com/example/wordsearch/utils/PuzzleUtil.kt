package com.example.wordsearch.utils

import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.data.PuzzleProgress

object PuzzleUtils {
    fun getPuzzleProgress(puzzles: List<Puzzle>): List<PuzzleProgress> =
        puzzles.map { puzzle ->
            val completedParts =
                puzzle.parts.count { it.isCompleted } // Assuming each part has `isCompleted`
            PuzzleProgress(
                puzzleId = puzzle.id,
                completedParts = completedParts,
                totalParts = puzzle.parts.size,
            )
        }
}
