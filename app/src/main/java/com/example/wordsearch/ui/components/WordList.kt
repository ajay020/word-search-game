package com.example.wordsearch.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordsearch.viewModels.Word

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordList(
    modifier: Modifier = Modifier,
    words: List<Word>,
) {
    FlowRow(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        maxItemsInEachRow = 3,
    ) {
        words.forEach { word: Word ->
            WordItem(word = word)
        }
    }
}

@Composable
fun WordItem(word: Word) {
    Box(
        modifier =
            Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = word.text,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (word.found) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (word.found) word.color else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun WordListPreviw() {
    val words =
        listOf(
            Word("Bat", false),
            Word("Bat", false),
            Word("Bat", true),
            Word("Bat", false),
            Word("Bat", false),
            Word("Bat", false),
        )
    WordList(words = words, modifier = Modifier.fillMaxWidth())
}
