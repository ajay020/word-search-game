package com.example.wordsearch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchGridTopbar(
    title: String,
    coins: Int,
    onCloseClick: () -> Unit,
    onHintClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.background(Color.White.copy(alpha = 1f)),
        colors =
            TopAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = Color.White,
                navigationIconContentColor = Color.Black,
                titleContentColor = Color.Black,
                scrolledContainerColor = Color.Black,
            ),
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Pause",
                )
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .background(Color.Yellow, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_monetization_on),
                    contentDescription = "Coins",
                    tint = Color.Blue,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = coins.toString(),
                    fontSize = 14.sp,
                    color = Color.Blue,
                )
            }
            IconButton(onClick = onHintClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_lightbulb),
                    contentDescription = "Light bulb",
                    tint = Color.Red,
                )
            }
            IconButton(onClick = {onSettingsClick()}) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Setting icon",
                    tint = Color.DarkGray
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchGridTopBarPreivew() {
    SearchGridTopbar(
        title = "settings",
        coins = 100,
        onCloseClick = { /*TODO*/ },
        onHintClick = { },
        onSettingsClick = {}
    )
}
