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
import com.example.wordsearch.ui.screens.PuzzleSelectionScreen
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
        composable("puzzle/{level}") { backStackEntry ->
            val puzzleLevel = backStackEntry.arguments?.getString("level")?.toIntOrNull() ?: 1
            PuzzleSelectionScreen(
                level = puzzleLevel,
                navigateToGameScreen = { level: Int, id: Int ->
                    navController.navigate("game/$level/$id")
                },
            )
        }
        composable("game/{level}/{id}") { backStackEntry ->
            // Navigate to GameScreen
            // Pass the level and id from the arguments
            val level = backStackEntry.arguments?.getString("level")?.toIntOrNull() ?: 1
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            GameScreen(
                level = level,
                puzzleId = id,
                navigateToHomeScreen = {
//                    backStackEntry.arguments?.clear()
                    navController.popBackStack("home", inclusive = false)
                },
            )
        }
    }
}
