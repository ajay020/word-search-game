package com.example.wordsearch.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("LongParameterList")
@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp),
    backgroundColor: Color = Color.Blue,
    contentColor: Color = Color.White,
    shape: RoundedCornerShape = CircleShape,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    enabled: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    Button(
        modifier = modifier,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
        onClick = onClick,
        shape = shape,
        enabled = enabled,
    ) {
        if (text.isEmpty())
            {
                content()
            } else {
            Text(
                text = text,
                style = textStyle,
                modifier = Modifier.padding(paddingValues),
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
    Column {
        AppButton(
            modifier = Modifier.padding(6.dp),
            shape = CircleShape,
            textStyle =
                TextStyle(
                    fontSize = 18.sp,
                ),
            text = "Hello",
            onClick = { /*TODO*/ },
        )
    }
}
