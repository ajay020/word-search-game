package com.example.wordsearch.data

import android.content.Context
import com.example.wordsearch.repository.PuzzleProgressRepository
import com.example.wordsearch.repository.PuzzleRepository
import com.example.wordsearch.utils.PuzzleProgressManager
import com.example.wordsearch.utils.SoundSettingsManager

interface AppContainer {
    val puzzleProgressRepository: PuzzleProgressRepository
    val puzzleRepository: PuzzleRepository
    val puzzleProgressManager: PuzzleProgressManager
    val soundSettingsManager: SoundSettingsManager
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    override val puzzleProgressRepository = PuzzleProgressRepository(context)
    override val puzzleRepository = PuzzleRepository(context)
    override val puzzleProgressManager = PuzzleProgressManager(context)
    override val soundSettingsManager = SoundSettingsManager(context)
}
