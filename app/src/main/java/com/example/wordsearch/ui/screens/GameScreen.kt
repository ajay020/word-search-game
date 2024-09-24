@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.ui.components.WordGrid
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson

const val TAG = "WordSearchScreen"

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    level: Int,
    puzzleId: Int,
) {
    val fileName = "level_$level.json"
    val context = LocalContext.current

    var puzzle by remember { mutableStateOf<Puzzle?>(null) }

    LaunchedEffect(fileName) {
        puzzle =
            loadPuzzlesFromJson(context, fileName).first { it.id == puzzleId }
        println("puzzles: $puzzle")
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
    ) {
        puzzle?.let {
            WordGrid( puzzle = puzzle!!)
        }
    }
}
