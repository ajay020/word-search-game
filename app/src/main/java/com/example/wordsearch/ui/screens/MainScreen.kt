package com.example.wordsearch.ui.screens

import SettingsDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.viewModels.SearchGridState
import com.example.wordsearch.viewModels.SearchGridViewModel

@Composable
fun MainScreen(
    navigateToGameScreen: (Int) -> Unit,
    viewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
) {
    val searchGridUiState = viewModel.uiState.value
    var showSettingDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            MainScreenTopBar(
                modifier = Modifier.background(Color.Green),
                onSettingsClick = { showSettingDialog = true },
            )
        },
    ) {
        MainScreenContent(
            modifier = Modifier.padding(it),
            searchGridUiState = searchGridUiState,
            navigateToGameScreen = navigateToGameScreen,
        )

        if (showSettingDialog) {
            SettingsDialog(
                onDismiss = { showSettingDialog = false },
                onSoundToggle = { viewModel.toggleSound() },
                isSoundEnabled = searchGridUiState.isSoundEnabled,
            )
        }
    }
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    navigateToGameScreen: (Int) -> Unit,
    searchGridUiState: SearchGridState,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize(),
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
    }
}

@Composable
fun MainScreenTopBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .background(Color.Green)
                .padding(8.dp)
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(onClick = { onSettingsClick() }) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "settings icon",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreenContent(
        navigateToGameScreen = {},
        searchGridUiState = SearchGridState(),
    )

    MainScreenTopBar(
        onSettingsClick = { },
    )
}
