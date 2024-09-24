package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Buttons for each level
            (1..5).forEach { level ->
                Button(
                    onClick = { navController.navigate("puzzle/$level") },
                    modifier =
                        Modifier
                            .width(200.dp) // Set width for uniform button size
                            .height(50.dp), // Set height for uniform button size
                ) {
                    Text(text = "Level $level", fontSize = 18.sp)
                }
            }
        }
    }
}
