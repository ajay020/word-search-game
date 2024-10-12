package com.example.wordsearch.ui.viewModels

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.R
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.repository.PuzzleProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    val puzzleProgressRepository: PuzzleProgressRepository,
    application: Application,
) : AndroidViewModel(application) {
    private var mediaPlayer: MediaPlayer? = null
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    fun toggleSound() {
        _homeState.value = _homeState.value.copy(soundEnabled = !_homeState.value.soundEnabled)
    }

    fun toggleMusic() {
        val musicEnabled = !_homeState.value.musicEnabled
        _homeState.value = _homeState.value.copy(musicEnabled = musicEnabled)

        if (musicEnabled) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun changeBackground(newBackground: String) {
        _homeState.value = _homeState.value.copy(background = newBackground)
    }

    fun updateCoins(coins: Int) {
        _homeState.value = _homeState.value.copy(coins = coins)
    }

    init {
        // Initialize and play soothing music
        startBackgroundMusic()
    }

    fun getPuzzleProgress(puzzleId: Int) = puzzleProgressRepository.getPuzzleProgress(puzzleId)

    private fun startBackgroundMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplication(), R.raw.music)
            mediaPlayer?.isLooping = true // Set looping
            mediaPlayer?.start()
        }
    }

    fun pauseMusic() {
        if (_homeState.value.musicEnabled) {
            mediaPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (_homeState.value.musicEnabled) {
            mediaPlayer?.start()
        }
    }

    // inject repository
    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    // Get the application instance
                    val application = this[APPLICATION_KEY] as WordSearchApplication

                    // Get the repository from your application container
                    val puzzleProgressRepository = application.container.puzzleProgressRepository

                    // Pass the repository and application to HomeViewModel
                    HomeViewModel(
                        puzzleProgressRepository = puzzleProgressRepository,
                        application = application, // Pass the application here
                    )
                }
            }
    }
}

data class HomeState(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val background: String = "default",
    val coins: Int = 0,
)
