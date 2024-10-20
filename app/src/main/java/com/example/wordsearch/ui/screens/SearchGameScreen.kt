package com.example.wordsearch.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.ui.components.SearchGrid
import com.example.wordsearch.ui.components.SearchGridTopbar
import com.example.wordsearch.viewModels.SearchGameViewModal
import com.example.wordsearch.viewModels.SearchGridViewModel

@Composable
fun SearchGameScreen(
    modifier: Modifier = Modifier,
    searchGameViewModal: SearchGameViewModal = viewModel(),
    navigateToMainScreen: () -> Unit,
) {
    val searchGridViewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory)
    val uiState by searchGameViewModal.uiState.collectAsState()

    Scaffold(
        topBar = {
            SearchGridTopbar(
                title = "Word Search",
                coins = uiState.coins,
                onCloseClick = { navigateToMainScreen() },
                onHintClick = {
                    if (uiState.availableHints > 0) {
                        searchGameViewModal.useHint()
                        searchGridViewModel.highlightFirstCharacter()
                    }
                },
            )
        },
    ) {
        SearchGrid(
            modifier = modifier.padding(it),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchGreenScreenPreview() {
//    SearchGameScreen(navigateToMainScreen = {
//        navController.navigate("main")
//    })
}
