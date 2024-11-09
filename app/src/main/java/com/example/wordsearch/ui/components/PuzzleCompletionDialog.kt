package com.example.wordsearch.ui.components

import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.wordsearch.R

@Composable
fun PuzzleCompletionDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onNextPuzzle: () -> Unit,
) {
    Dialog(onDismissRequest = {  }) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1f)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                modifier = Modifier.size(128.dp),
                painter = painterResource(id = R.drawable.ic_coins),
                contentDescription = ""
            )
            Text(
                text = "You've won 25 coins!",
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = onNextPuzzle,
                text = "Next",
                shape = CircleShape,
            )
            Spacer(modifier = Modifier.height(10.dp))
            AppOutlinedButton(
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = onDismiss,
                text = "Close"
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun PuzzleCompletionDialogPreview() {
    PuzzleCompletionDialog(onDismiss = { /*TODO*/ }) {
        
    }
}
