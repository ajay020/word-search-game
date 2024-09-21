@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Suppress("ktlint:standard:function-naming")
@Composable
fun DrawingDemo(modifier: Modifier = Modifier) {
    var pointerOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var paths by remember {
        mutableStateOf(listOf<Offset>())
    }
    var currentPath by remember {
        mutableStateOf<Offset?>(null)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput("dragging") {
                    detectDragGestures { change, dragAmount ->
                        pointerOffset += dragAmount
                        currentPath = pointerOffset
                        paths = paths + pointerOffset
                        change.consume()
                    }
                }.onSizeChanged {
                    pointerOffset = Offset(it.width / 2f, it.height / 2f)
                }.drawWithContent {
                    drawContent()

                    // draws a fully black area with a small keyhole at pointerOffset that’ll show part of the UI.
                    drawRect(
                        Brush.radialGradient(
                            listOf(Color.Transparent, Color.Black),
                            center = pointerOffset,
                            radius = 100.dp.toPx(),
                        ),
                    )
                },
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Green),
        ) {
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun DrawBehindDemo(modifier: Modifier = Modifier) {

    Box (
        modifier = Modifier
            .padding(4.dp)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            "Hello Compose!",
            modifier =
            Modifier
                .drawBehind {

                    drawLine(
                        strokeWidth = size.height-8.dp.toPx(),
                        start = Offset( 0f, size.height/2),
                        end =  Offset( size.width, size.height/2),
                        color = Color.Red,
                    )

                    drawRoundRect(
                        color = Color.Gray.copy(alpha = 0.2f),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                    )
                }.padding(8.dp),
        )
    }

}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
private fun DrawingDemoPreview() {
//    DrawingDemo()
    DrawBehindDemo()
}
