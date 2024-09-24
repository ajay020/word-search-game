package com.example.wordsearch.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Line(
    val offsets: List<Offset>,
    val color: Color = Color.Blue,
    val strokeWidth: Float = 60f,
)

val orange = Color(0xFFEE693F)
val green = Color(0xFF1BCC1B)
val blue = Color(0xFF4E79E9)
val lightPink = Color(0xFFE92E6E)
val yellow = Color(0xFFEEDB2C)
val magenta = Color(0xFFB14DE7)
val lightBlue = Color(0xFF44A0EB)
val breakRed = Color(0xFFEC3D42)

val colors =
    listOf(
        lightPink,
        blue,
        green,
        orange,
        yellow,
        magenta,
        lightBlue,
        breakRed,
    )

fun getRandomColor(): Color = colors.random()
