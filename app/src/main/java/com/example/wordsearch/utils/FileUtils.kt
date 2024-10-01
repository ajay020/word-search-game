package com.example.wordsearch.utils

import android.content.Context
import com.example.wordsearch.data.Puzzle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object FileUtils {
    fun loadPuzzlesFromJson(
        context: Context,
        fileName: String = "puzzles.json",
    ): List<Puzzle> {
        val jsonString: String
        try {
            jsonString =
                context.assets
                    .open(fileName)
                    .bufferedReader()
                    .use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val puzzleListType = object : TypeToken<List<Puzzle>>() {}.type
        return Gson().fromJson(jsonString, puzzleListType)
    }
}
