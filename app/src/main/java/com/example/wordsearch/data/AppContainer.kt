package com.example.wordsearch.data

import android.content.Context
import com.example.wordsearch.repository.PuzzleProgressRepository

interface AppContainer {
    val puzzleProgressRepository: PuzzleProgressRepository
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    override val puzzleProgressRepository = PuzzleProgressRepository(context)
}
