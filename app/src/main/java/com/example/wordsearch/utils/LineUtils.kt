package com.example.wordsearch.utils

import android.util.Log
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

object LineUtils {
    private const val TOLERANCE = 3.5f

    // Compare two Offsets with a tolerance
    private fun areOffsetsEqual(
        offset1: Offset,
        offset2: Offset,
        tolerance: Float = 0.1f,
    ): Boolean = abs(offset1.x - offset2.x) < tolerance && abs(offset1.y - offset2.y) < tolerance

    // Check if a line is already in the set with approximate comparison
    private fun isLineAlreadySelected(
        selectedLines: Set<Pair<Offset, Offset>>,
        newLine: Pair<Offset, Offset>,
        tolerance: Float = 0.1f,
    ): Boolean =
        selectedLines.any { existingLine ->
            (
                areOffsetsEqual(existingLine.first, newLine.first, tolerance) &&
                    areOffsetsEqual(existingLine.second, newLine.second, tolerance)
            ) ||
                (
                    areOffsetsEqual(existingLine.first, newLine.second, tolerance) &&
                        areOffsetsEqual(existingLine.second, newLine.first, tolerance)
                )
        }

    // Add a line to selected lines if it's not already present
    fun addLineToSelected(
        selectedLines: MutableSet<Pair<Offset, Offset>>,
        newLine: Pair<Offset, Offset>,
        tolerance: Float = TOLERANCE,
    ): Set<Pair<Offset, Offset>> =
        if (!isLineAlreadySelected(selectedLines, newLine, tolerance)) {
            Log.d("LineUtils", "Line added: ${ selectedLines + newLine}")
            selectedLines + newLine
        } else {
            Log.d(
                "LineUtils",
                "Line already selected: ${
                    isLineAlreadySelected(
                        selectedLines,
                        newLine,
                        tolerance,
                    )
                }",
            )
            selectedLines
        }
}
