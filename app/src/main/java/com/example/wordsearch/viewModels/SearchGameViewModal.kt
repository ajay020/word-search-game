package com.example.wordsearch.viewModels

import android.app.Application
import android.media.MediaPlayer
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.R
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.utils.SoundSettingsManager
import com.example.wordsearch.utils.ThemePreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

const val HINT_COST = 10

class SearchGameViewModal(
    private val themePreferenceManager: ThemePreferenceManager,
    private val soundSettingsManager: SoundSettingsManager,
    application: Application,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SearchGameState())
    val uiState: StateFlow<SearchGameState> = _uiState.asStateFlow()
    private var mediaPlayer: MediaPlayer? = null

    init {
        _uiState.value = _uiState.value.copy(backgroundImgRes = themePreferenceManager.getBackgroundImage())
        // Load sound setting on initialization
        _uiState.value = _uiState.value.copy(musicEnabled = soundSettingsManager.isMusicEnabled())
        // Initialize and play soothing music
//        startBackgroundMusic()
    }

    fun startBackgroundMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplication(), R.raw.piano_music_soft)
            mediaPlayer?.isLooping = true // Set looping
            if (_uiState.value.musicEnabled) {
                mediaPlayer?.start()
            }
        }
    }

    fun pauseMusic() {
        if (_uiState.value.musicEnabled) {
            mediaPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (_uiState.value.musicEnabled) {
            mediaPlayer?.start()
        }
    }

    fun toggleMusic() {
        val musicEnabled = !_uiState.value.musicEnabled
        _uiState.value = _uiState.value.copy(musicEnabled = musicEnabled)
        soundSettingsManager.saveMusicEnabled(musicEnabled)

        if (musicEnabled) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
    }

    //
    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Function to use a hint
    fun useHint() {
        if (uiState.value.coins > 0) {
            _uiState.update { currentState ->
                currentState.copy(
                    coins = currentState.coins - HINT_COST,
                    availableHints = currentState.availableHints - 1,
                )
            }
        } else {
            // Handle no more coins available, e.g., show a message
        }
    }

    // Function to update coins
    fun updateCoins(newCoins: Int) {
        _uiState.update { currentState ->
            currentState.copy(coins = newCoins)
        }
    }

    companion object {
        val FACTORY =
            viewModelFactory {
                initializer {
                    val application = this[APPLICATION_KEY] as WordSearchApplication
                    SearchGameViewModal(
                        themePreferenceManager = application.container.themePreferenceManager,
                        soundSettingsManager = application.container.soundSettingsManager,
                        application = application,
                    )
                }
            }
    }
}

data class SearchGameState(
    val coins: Int = 50,
    val musicEnabled: Boolean = true,
    val availableHints: Int = coins / HINT_COST,
    @DrawableRes val backgroundImgRes: Int = R.drawable.sky,
)
