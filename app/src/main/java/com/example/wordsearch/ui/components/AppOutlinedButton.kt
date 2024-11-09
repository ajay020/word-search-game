package com.example.wordsearch.ui.components

import android.view.RoundedCorner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("LongParameterList")
@Composable
fun AppOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.Blue,
        fontWeight = FontWeight.Bold
    ),
    shape: RoundedCornerShape = CircleShape,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    enabled: Boolean = true
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun PuzzleCompletionDialogPreview() {
    Column {
        AppOutlinedButton(
            modifier = Modifier.padding(6.dp),
            shape = CircleShape,
            text = "Hello",
            onClick = { /*TODO*/ }
        )
    }
}
