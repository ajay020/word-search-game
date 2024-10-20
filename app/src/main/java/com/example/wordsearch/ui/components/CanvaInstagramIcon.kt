package com.example.wordsearch.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Suppress("ktlint:standard:function-naming")
@Composable
fun InstagramIcon(modifier: Modifier) {
    val instaColors = listOf(Color.Yellow, Color.Red, Color.Magenta)
    Canvas(
        modifier =
        Modifier
            .size(200.dp)
            .padding(16.dp),
    ) {
        drawRoundRect(
            brush = Brush.linearGradient(colors = instaColors),
            cornerRadius = CornerRadius(60f, 60f),
            style = Stroke(width = 25f, cap = StrokeCap.Round),
        )
        drawCircle(
            brush = Brush.linearGradient(colors = instaColors),
            radius = 55f,
            style = Stroke(width = 25f, cap = StrokeCap.Round),
        )
        drawCircle(
            brush = Brush.linearGradient(colors = instaColors),
            radius = 13f,
            center = Offset(this.size.width * .80f, this.size.height * 0.20f),
        )
    }
}

@Composable
fun messengerIcon() {
    val colors = listOf(Color(0xFF02b8f9), Color(0xFF0277fe))
    Canvas(
        modifier =
            Modifier
                .size(100.dp)
                .padding(16.dp),
    ) {
        val trianglePath =
            Path().let {
                it.moveTo(this.size.width * .20f, this.size.height * .70f)
                it.lineTo(this.size.width * .20f, this.size.height * 0.95f)
                it.lineTo(this.size.width * .37f, this.size.height * 0.86f)
                it.close()
                it
            }

        val electricPath =
            Path().let {
                it.moveTo(this.size.width * .20f, this.size.height * 0.60f)
                it.lineTo(this.size.width * .45f, this.size.height * 0.35f)
                it.lineTo(this.size.width * 0.59f, this.size.height * 0.48f)
                it.lineTo(this.size.width * 0.78f, this.size.height * 0.35f)
                it.lineTo(this.size.width * 0.94f, this.size.height * 0.68f)
                it.lineTo(this.size.width * 0.75f, this.size.height * 0.48f)

                it.lineTo(this.size.width * 0.54f, this.size.height * 0.60f)
                it.lineTo(this.size.width * 0.43f, this.size.height * 0.45f)

                it.close()
                it
            }

        drawOval(
            Brush.verticalGradient(colors = colors),
            size = Size(this.size.width, this.size.height * 0.95f)
        )

        drawPath(
            path = trianglePath,
            Brush.verticalGradient(colors = colors),
            style = Stroke(width = 15f, cap = StrokeCap.Round),
        )

        drawPath(path = electricPath, color = Color.White)
    }
}

@Composable
private fun getGooglePhotosIcon() {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(0.dp)
    ) {
        drawArc(
            color = Color(0xFFf04231),
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(size.width * .25f, 0f)
        )
        drawArc(
            color = Color(0xFF4385f7),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(size.width * .50f, size.height * .25f)
        )
        drawArc(
            color = Color(0xFF30a952),
            startAngle = 0f,
            sweepAngle = -180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(0f, size.height * .25f)
        )

        drawArc(
            color = Color(0xFFffbf00),
            startAngle = 270f,
            sweepAngle = -180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(size.width * .25f, size.height * .50f)
        )


    }
}

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
private fun PreviewInsta() {
//    InstagramIcon(modifier = Modifier)
//    messengerIcon()
    getGooglePhotosIcon()
}
