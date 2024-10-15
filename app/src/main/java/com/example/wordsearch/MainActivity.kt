@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordsearch.ui.screens.GameScreen
import com.example.wordsearch.ui.screens.HomeScreen
import com.example.wordsearch.ui.theme.WordSearchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
