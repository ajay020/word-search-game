package com.example.wordsearch.utils

import android.content.Context

class PuzzleProgressManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("puzzle_progress", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_LEVEL = "current_level"
        private const val KEY_TOTAL_WORD_FOUND = "total_word_found"
    }

    fun saveCurrentLevel(level: Int) {
        sharedPreferences.edit().putInt(KEY_CURRENT_LEVEL, level).apply()
    }

    fun getCurrentLevel(): Int {
        return sharedPreferences.getInt(KEY_CURRENT_LEVEL, 0) // Default to level 0 if not found
    }

    fun getTotalWordFound(): Int {
        return sharedPreferences.getInt(KEY_TOTAL_WORD_FOUND, 0) // Default to level 0 if not found
    }

    fun saveSearchedWordsCount(wordCount: Int) {
        val currentWordCount = getTotalWordFound()
        val updatedWordCount = currentWordCount + wordCount
        sharedPreferences.edit().putInt(KEY_TOTAL_WORD_FOUND, updatedWordCount).apply()
    }

    fun resetProgress() {
        sharedPreferences.edit().remove(KEY_CURRENT_LEVEL).apply()
    }
}
