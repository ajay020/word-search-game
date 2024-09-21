package com.example.wordsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Suppress("ktlint:standard:function-naming")
@Composable
fun DrawingCanvas(modifier: Modifier) {
    var paths by remember { mutableStateOf(listOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentPathPoints by remember { mutableStateOf(listOf<Offset>()) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Gray)
                .border(1.dp, Color.Black),
    ) {
        Canvas(
            modifier =
                Modifier
                    .background(Color.Cyan)
                    .border(1.dp, Color.Red)
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath =
                                    Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                currentPathPoints = listOf(offset)
                                Log.d("DrawingCanvas", "Drag start at : $offset")
                            },
                            onDrag = { change, _ ->
                                Log.d("DrawingCanvas", "Drag: ${change.position}")
                                val newPoint = change.position
                                currentPath?.lineTo(newPoint.x, newPoint.y)
                                currentPathPoints += newPoint
                            },
                            onDragEnd = {
                                Log.d("DrawingCanvas", "Drag ended")
                                currentPath?.let {
                                    paths = paths + it
                                    currentPath = null
                                    currentPathPoints = listOf()
                                }
                            },
                        )
                    },
        ) {
            Log.d("DrawingCanvas", "Canvas is drawing the line")

            paths.forEach { path ->
                drawPath(
                    path = path,
                    color = Color.Blue.copy(alpha = 0.2f),
                    style = Stroke(width = 50f),
                )
            }

            if (currentPathPoints.isNotEmpty()) {
                drawPoints(
                    points = currentPathPoints,
                    pointMode = PointMode.Polygon,
                    color = Color.Red.copy(alpha = 0.2f),
                    cap = StrokeCap.Round,
                    strokeWidth = 50f,
                    blendMode = BlendMode.Color,
                )
            }
        }
    }
}
//
// data class Line(
//    val start: Offset,
//    val end: Offset,
//    val strokeWidth: Dp = 1.dp,
//    val color: Color = Color.Blue,
// )

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
private fun CanvasPreview() {
    DrawingCanvas(modifier = Modifier.fillMaxSize())
}
