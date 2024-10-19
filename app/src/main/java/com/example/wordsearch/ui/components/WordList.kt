package com.example.wordsearch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wordsearch.ui.viewModels.Word

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordList(
    modifier: Modifier = Modifier,
    words: List<Word>
) {
    FlowRow(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        maxItemsInEachRow = 3,
    ) {
        words.forEach{ word: Word ->
            WordItem(word = word)
        }
    }
}

@Composable
fun WordItem(word: Word) {
    Box(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word.text,
            style = MaterialTheme.typography.bodyMedium .copy(
                textDecoration = if (word.found) TextDecoration.LineThrough else TextDecoration.None,
                color = if (word.found) Color.Gray else Color.Black
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WordListPreviw() {
    val words = listOf(
        Word("Bat", false ),
        Word("Bat", false ),
        Word("Bat", true ),
        Word("Bat", false ),
        Word("Bat", false ),
        Word("Bat", false ),
    )
    WordList(words = words)
}
