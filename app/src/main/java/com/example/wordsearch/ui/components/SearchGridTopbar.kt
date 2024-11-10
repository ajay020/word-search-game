package com.example.wordsearch.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordsearch.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchGridTopbar(
    remainingTime: Int,
    coins: Int,
    onCloseClick: () -> Unit,
    onHintClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors =
            androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
        title = {
            TimerText(remainingTime = remainingTime)
        },
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Pause",
                    tint = Color.Black,
                )
            }
        },
        actions = {
            Row(
                modifier =
                    Modifier
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_coin),
                    contentDescription = "Coins",
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = coins.toString(),
                    fontSize = 14.sp,
                    color = Color.Blue,
                )
            }
//            IconButton(onClick = onHintClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_lightbulb),
//                    contentDescription = "Light bulb",
//                    tint = Color.Red,
//                )
//            }
//            IconButton(onClick = { onSettingsClick() }) {
//                Icon(
//                    Icons.Default.Settings,
//                    contentDescription = "Setting icon",
//                    tint = Color.DarkGray,
//                )
//            }
        },
    )
}

@Composable
fun TimerText(
    modifier: Modifier = Modifier,
    remainingTime: Int,
) {
    Row(
        modifier = Modifier,
//            .background(Color.Blue.copy(alpha = 0.7f), shape = RoundedCornerShape(4.dp))
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier =
                modifier
                    .padding(6.dp),
            text = formatTime(remainingTime),
            fontSize = 24.sp,
            color = Color.Blue,
            textAlign = TextAlign.Center,
        )
    }
}

// Helper function to format time
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

@Preview(showBackground = true)
@Composable
private fun SearchGridTopBarPreivew() {
    SearchGridTopbar(
        remainingTime = 110,
        coins = 100,
        onCloseClick = { /*TODO*/ },
        onHintClick = { },
        onSettingsClick = {},
    )
}
