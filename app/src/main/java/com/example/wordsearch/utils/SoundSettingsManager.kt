package com.example.wordsearch.utils

import android.content.Context

class SoundSettingsManager(
    context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("sound_settings", Context.MODE_PRIVATE)

    companion object {
        private const val MUSIC_KEY = "is_music_enabled"
        private const val SOUND_KEY = "is_sound_enabled"
    }

    fun isSoundEnabled(): Boolean = sharedPreferences.getBoolean(SOUND_KEY, true)

    fun saveSoundEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(SOUND_KEY, isEnabled).apply()
    }

    fun isMusicEnabled(): Boolean = sharedPreferences.getBoolean(MUSIC_KEY, true)

    fun saveMusicEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(MUSIC_KEY, isEnabled).apply()
    }
}
