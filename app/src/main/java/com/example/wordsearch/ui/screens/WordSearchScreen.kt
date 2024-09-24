@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.wordsearch.ui.components.WordGrid
import com.example.wordsearch.ui.viewModels.WordSearchViewModel

const val TAG = "WordSearchScreen"

@Suppress("ktlint:standard:function-naming")
@Composable
fun WordSearchScreen(
    modifier: Modifier,
) {
    ScreenContent(
        modifier = modifier,
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
    ) {
        WordGrid()
    }
}

