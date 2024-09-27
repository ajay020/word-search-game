package com.example.wordsearch.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CongratsDialog(
    onDismiss: () -> Unit,
    onNextGame: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Congratulations!") },
        text = { Text("You've completed the puzzle. Ready for the next challenge?") },
        confirmButton = {
            Button(onClick = { onNextGame() }) {
                Text("Next Game")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
    )
}
