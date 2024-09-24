package com.example.wordsearch.utils

import android.content.Context
import com.example.wordsearch.data.Puzzle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FileUtils {
    fun loadPuzzlesFromJson(
        context: Context,
        fileName: String,
    ): List<Puzzle> {
        val jsonString: String =
            context.assets
                .open(fileName)
                .bufferedReader()
                .use { it.readText() }
        val listType = object : TypeToken<List<Puzzle>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

//    fun loadWordListFromAssets(context: Context, fileName: String): List<String>? {
//        return try {
//            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
//            Gson().fromJson(jsonString, Array<String>::class.java).toList()
//        } catch (e: IOException) {
//            // Handle exception(e.g., log error, return empty list)
//            null
//        }
//    }
}
