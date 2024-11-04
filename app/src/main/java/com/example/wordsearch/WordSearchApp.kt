package com.example.wordsearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WordSearchApp() {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(
            onSplashComplete = { showSplash = false },
        )
    } else {
        MainApp() // Navigate to the main screen after the splash
    }
}

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashComplete: () -> Unit,
) {
    // Set a delay for the splash screen (e.g., 2 seconds)
    LaunchedEffect(Unit) {
        delay(2000)
        onSplashComplete()
    }

    // UI for the splash screen
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color(0xFF1E88E5)),
        // Set the background color
        contentAlignment = Alignment.Center,
    ) {
        // Display a logo or animated component
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your logo drawable
                contentDescription = "App Logo",
                modifier = Modifier.size(128.dp),
                tint = Color.White,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Word Search Game",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
