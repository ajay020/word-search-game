package com.example.wordsearch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.viewModels.SearchGridViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigateToGameScreen: (Int) -> Unit,
    viewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
) {
    MainScreenContent(
        viewModel = viewModel,
        navigateToGameScreen = navigateToGameScreen,
    )
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    viewModel: SearchGridViewModel,
    navigateToGameScreen: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Current Puzzle: Level ${viewModel.currentLevel + 1}",
        )

        Button(
            onClick = { navigateToGameScreen(viewModel.currentLevel) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Start Level ${viewModel.currentLevel + 1}")
        }

        if (viewModel.currentLevel > 0) {
            Text(
                text = "Puzzles Solved: ${viewModel.currentLevel}",
                modifier = Modifier.padding(top = 16.dp)
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
