package com.example.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.wordsearch.ui.screens.WordSearchScreen
import com.example.wordsearch.ui.theme.WordSearchTheme
import com.example.wordsearch.ui.viewModels.WordSearchViewModel

class MainActivity : ComponentActivity() {
    // Initialize your ViewModel here
    private val viewModel = WordSearchViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordSearchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WordSearchScreen(
                        viewModel = viewModel,
                        modifier =
                            Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                    )
//
                }
            }
        }
    }
}
