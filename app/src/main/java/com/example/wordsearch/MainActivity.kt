@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch

import android.os.Build.*
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordsearch.ui.screens.GameScreen
import com.example.wordsearch.ui.screens.HomeScreen
import com.example.wordsearch.ui.theme.WordSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make the activity full screen by hiding the status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.statusBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Additional handling for older devices (API level 24 and below)
        if (VERSION.SDK_INT < VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Stable layout, don't adjust resizing
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Draw behind status bar
            )
        }

        enableEdgeToEdge()

        setContent {
            WordSearchTheme {
                MainApp()
            }
        }
    }
}

// Entry point for your app
@Composable
fun MainApp() {
    val navController = rememberNavController()

    // NavHost that holds all screens
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("game/{id}") { backStackEntry ->
            // Navigate to GameScreen
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            GameScreen(
                puzzleId = id,
                navigateToHomeScreen = {
                    navController.popBackStack("home", inclusive = false)
                },
            )
        }
    }
}
