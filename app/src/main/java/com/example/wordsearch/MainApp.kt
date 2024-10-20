package com.example.wordsearch

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wordsearch.ui.screens.MainScreen
import com.example.wordsearch.ui.screens.SearchGameScreen

// Entry point for your app
@Composable
fun MainApp() {
    val navController = rememberNavController()

    // NavHost that holds all screens
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                navigateToGameScreen = { id: Int ->
                    navController.navigate("game/$id")
                },
            )
        }
        composable("game/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            SearchGameScreen(
                navigateToMainScreen = {
                    navController.navigate("main")
                },
            )
        }
    }
}
