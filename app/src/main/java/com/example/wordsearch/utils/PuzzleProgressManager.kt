package com.example.wordsearch.utils

import android.content.Context

class PuzzleProgressManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("puzzle_progress", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_LEVEL = "current_level"
    }

    fun saveCurrentLevel(level: Int) {
        sharedPreferences.edit().putInt(KEY_CURRENT_LEVEL, level).apply()
    }

    fun getCurrentLevel(): Int {
        return sharedPreferences.getInt(KEY_CURRENT_LEVEL, 0) // Default to level 0 if not found
    }
}
