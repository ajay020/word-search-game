package com.example.wordsearch.utils

import android.content.Context

class SoundSettingsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("sound_settings", Context.MODE_PRIVATE)

    fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean("is_sound_enabled", true) // Default to true
    }

    fun saveSoundEnabled(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean("is_sound_enabled", isEnabled).apply()
    }
}