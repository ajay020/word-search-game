package com.example.wordsearch.ui.screens

import SettingsDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordsearch.R
import com.example.wordsearch.ui.components.ThemeSelectionDialog
import com.example.wordsearch.viewModels.MainViewModel
import com.example.wordsearch.viewModels.SearchGridState
import com.example.wordsearch.viewModels.SearchGridViewModel

@Composable
fun MainScreen(
    navigateToGameScreen: (Int) -> Unit,
    viewModel: SearchGridViewModel = viewModel(factory = SearchGridViewModel.Factory),
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory),
) {
    val searchGridUiState = viewModel.uiState.value
    val mainUiState by mainViewModel.uiState.collectAsState()

    var showSettingDialog by remember {
        mutableStateOf(false)
    }
    var showThemeDialog by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = mainUiState.backGroundImage),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Scaffold(
            modifier =
                Modifier
                    .background(Color.Transparent)
                    .fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            topBar = {
                MainScreenTopBar(
                    modifier = Modifier,
                    onSettingsClick = { showSettingDialog = true },
                    onThemeClicked = { showThemeDialog = true },
                )
            },
        ) { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                MainScreenContent(
                    modifier = Modifier.background(Color.Transparent),
                    searchGridUiState = searchGridUiState,
                    navigateToGameScreen = navigateToGameScreen,
                )
            }

            if (showSettingDialog) {
                SettingsDialog(
                    onDismiss = { showSettingDialog = false },
                    onSoundToggle = { viewModel.toggleSound() },
                    isSoundEnabled = searchGridUiState.isSoundEnabled,
                )
            }

            // Theme selection dialog
            if (showThemeDialog) {
                ThemeSelectionDialog(
                    onImageSelected = { selectedImage ->
                        mainViewModel.updateBackgroundImage(selectedImage)
                        showThemeDialog = false
                    },
                    onDismiss = { showThemeDialog = false },
                )
            }
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
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { navigateToGameScreen(searchGridUiState.currentLevel) },
            modifier =
                Modifier
                    .padding(bottom = 120.dp),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                text = "Start Level ${searchGridUiState.currentLevel + 1}",
                fontSize = 24.sp,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    onThemeClicked: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        colors =
            androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
        title = { /*TODO*/ },
        actions = {
            IconButton(onClick = { onThemeClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = "theme icon",
                )
            }

            IconButton(onClick = { onSettingsClick() }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "settings icon",
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
//    Box(
//        modifier =
//        Modifier
//            .fillMaxSize()
//            .background(Color.LightGray),
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.sky),
//            contentDescription = "Background",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//        MainScreenContent(
//            modifier = Modifier.background(Color.Transparent),
//            navigateToGameScreen = {},
//            searchGridUiState = SearchGridState(),
//        )
//    }

    MainScreenTopBar(
        onSettingsClick = { },
    )
}
