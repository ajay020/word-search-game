package com.example.wordsearch.utils

import android.content.Context
import com.example.wordsearch.R

class ThemePreferenceManager(
    private val context: Context,
) {
    private val sharedPreferences = context.getSharedPreferences("background_pref", Context.MODE_PRIVATE)

    companion object {
        private const val BACKGROUND_IMAGE_KEY = "background_image"
    }

    fun saveBackgroundImage(resourceId: Int) {
        sharedPreferences.edit().putInt(BACKGROUND_IMAGE_KEY, resourceId).apply()
    }

    fun getBackgroundImage(): Int = sharedPreferences.getInt(BACKGROUND_IMAGE_KEY, R.drawable.sky)
}
