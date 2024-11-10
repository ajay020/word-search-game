package com.example.wordsearch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularBtnBackground(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .size(size)
            .border(1.dp, Color.White, shape = CircleShape)
            .background(Color.Black.copy(alpha = 0.7f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
private fun SearchGreenScreenPreview() {
    CircularBtnBackground() {
        IconButton(
            onClick = {}
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Setting icon",
                tint = Color.White,
                modifier = Modifier.size(40.dp),
            )
        }
    }
}

