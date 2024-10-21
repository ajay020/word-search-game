package com.example.wordsearch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.viewModels.SearchGridState
import com.example.wordsearch.viewModels.SearchGridViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToGameScreen: (Int) -> Unit,
    viewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
) {
    val searchGridUiState = viewModel.uiState.value
    MainScreenContent(
        viewModel = viewModel,
        searchGridUiState = searchGridUiState,
        navigateToGameScreen = navigateToGameScreen,
    )
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    viewModel: SearchGridViewModel,
    navigateToGameScreen: (Int) -> Unit,
    searchGridUiState: SearchGridState,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Current Puzzle: Level ${searchGridUiState.currentLevel + 1}",
        )

        Button(
            onClick = { navigateToGameScreen(searchGridUiState.currentLevel) },
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text(text = "Start Level ${searchGridUiState.currentLevel + 1}")
        }

        if (searchGridUiState.currentLevel > 0) {
            Text(
                text = "Puzzles Solved: ${searchGridUiState.currentLevel}",
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
//    MainScreenContent(
//        navigateToGameScreen = {},
//        viewModel = SearchGridViewModel()
//    )
}
