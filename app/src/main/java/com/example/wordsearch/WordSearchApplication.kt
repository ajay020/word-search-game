package com.example.wordsearch

import android.app.Application
import com.example.wordsearch.data.AppContainer
import com.example.wordsearch.data.DefaultAppContainer

class WordSearchApplication : Application() {
    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
