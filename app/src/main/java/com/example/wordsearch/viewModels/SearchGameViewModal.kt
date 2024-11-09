package com.example.wordsearch.viewModels

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wordsearch.R
import com.example.wordsearch.WordSearchApplication
import com.example.wordsearch.utils.SoundSettingsManager
import com.example.wordsearch.utils.ThemePreferenceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val HINT_COST = 10
const val TIMER_DURATION = 120
const val EXTRA_TIMER_DURATION = 60

class SearchGameViewModal(
    private val themePreferenceManager: ThemePreferenceManager,
    private val soundSettingsManager: SoundSettingsManager,
    application: Application,
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(SearchGameState())
    val uiState: StateFlow<SearchGameState> = _uiState.asStateFlow()
    private var mediaPlayer: MediaPlayer? = null

    private val _remainingTime = MutableStateFlow(TIMER_DURATION)
    val remainingTime: StateFlow<Int> = _remainingTime

    private val _isTimerExpired = MutableStateFlow(false)
    val isTimerExpired: StateFlow<Boolean> = _isTimerExpired

    private var timerJob: Job? = null

    init {
        // Load sound setting on initialization
        _uiState.value =
            _uiState.value.copy(
                backgroundImgRes = themePreferenceManager.getBackgroundImage(),
                musicEnabled = soundSettingsManager.isMusicEnabled(),
            )
    }

    fun startTimer(seconds: Int = TIMER_DURATION) {
        Log.d("SearchGame", "on start timer")

        stopTimer() // Stop any existing timer
        _isTimerExpired.value = false
        _remainingTime.value = seconds
        runTimer()
    }

    private fun runTimer() {
        timerJob =
            viewModelScope.launch {
                while (_remainingTime.value > 0) {
                    delay(1000L) // Wait for 1 second
                    _remainingTime.value -= 1
                }
                _isTimerExpired.value = true // Timer finished
            }
    }

    fun resetTimer(seconds: Int = TIMER_DURATION) {
        stopTimer()
        startTimer(seconds)
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun resumeTimer() {
        if (_remainingTime.value > 0 && timerJob == null) {
            runTimer()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun addExtraTime(seconds: Int = EXTRA_TIMER_DURATION) {
        _remainingTime.value = seconds
        startTimer(EXTRA_TIMER_DURATION)
    }

    /**
     * Music controls
     */

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

    // Release resources when the ViewModel is cleared
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
    @DrawableRes val backgroundImgRes: Int = R.drawable.bg2,
)
