package com.example.wordsearch.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TestingUI(modifier: Modifier = Modifier) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        BoxWithConstraints(
            modifier =
                Modifier
//            .fillMaxSize()
                    .border(4.dp, Color.Blue)
                    .background(Color.Cyan),
        ) {
            val cellSize = maxWidth / 5

            Box(
                modifier =
                    Modifier
                        .background(Color.Blue)
                        .border(4.dp, Color.Red)
//                .clip(RectangleShape)
                        .size(cellSize * 5, cellSize * 5),
            ) {
                Canvas(
                    modifier =
                        Modifier
//                    .matchParentSize()
                            .border(4.dp, Color.Green)
                            .background(Color.Red),
                ) {
                    for (i in 0 until 5) {
                        for (j in 0 until 5) {
                            drawRect(
                                color = Color.Yellow,
                                topLeft = Offset(x = j * cellSize.toPx(), y = i * cellSize.toPx()),
                                size = Size(cellSize.toPx(), cellSize.toPx()),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TestingUIPreview() {
    TestingUI()
}
