package com.example.wordsearch.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.wordsearch.R

@Composable
fun TimerExpiredDialog(
    onRestartGame: () -> Unit,
    onExtendTime: () -> Unit,
) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Card(
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Time's Up!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    modifier = Modifier.size(128.dp),
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    onClick = onExtendTime,
                ) {
                   Row (
                       verticalAlignment = Alignment.CenterVertically
                   ){
                       Text(text = "Buy Timer Boost")
                       Spacer(modifier = Modifier.width(9.dp))
                       Image(
                           modifier = Modifier.size(28.dp),
                           painter = painterResource(id = R.drawable.ic_coin),
                           contentDescription = "coin icon"
                       )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    onClick = onRestartGame,
                ) {
                    Text(text = "Restart")
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun TiemerExpirationDialogPreview() {
    TimerExpiredDialog(onRestartGame = { /*TODO*/ }) {
    }
}
