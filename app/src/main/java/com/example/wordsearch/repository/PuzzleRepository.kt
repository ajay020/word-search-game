package com.example.wordsearch.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Puzzle(
    val id: Long,
    val grid: List<List<Char>>,
    val words: List<String>,
)

data class PuzzleJson(
    val id: Long,
    val grid: List<String>, // Keep the grid as List<String> to match the JSON
    val wordsToFind: List<String>,
)

class PuzzleRepository(
    private val context: Context,
) {
    private val gson = Gson()
    private var puzzles: List<Puzzle> = emptyList()

    suspend fun loadPuzzles() {
        withContext(Dispatchers.IO) {
            val json =
                context.assets
                    .open("puzzles.json")
                    .bufferedReader()
                    .use { it.readText() }
            val puzzlesFromJson: List<PuzzleJson> = gson.fromJson(json, object : TypeToken<List<PuzzleJson>>() {}.type)
            puzzles =
                puzzlesFromJson.map { puzzleJson ->
                    Puzzle(
                        id = puzzleJson.id,
                        grid = puzzleJson.grid.map { it.toList() }, // Convert each string to a List<Char>
                        words = puzzleJson.wordsToFind,
                    )
                }
        }
    }

    fun getPuzzleByIndex(index: Int): Puzzle? =
        if (index in puzzles.indices) {
            puzzles[index]
        } else {
            null // Return null if index is out of bounds
        }

    fun getTotalPuzzles(): Int = puzzles.size
}
