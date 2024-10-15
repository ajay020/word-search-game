package com.example.wordsearch.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wordsearch.R
import com.example.wordsearch.data.Puzzle
import com.example.wordsearch.data.PuzzleProgress
import com.example.wordsearch.ui.viewModels.HomeState
import com.example.wordsearch.ui.viewModels.HomeViewModel
import com.example.wordsearch.utils.FileUtils.loadPuzzlesFromJson

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val context = LocalContext.current
    var puzzles by remember { mutableStateOf<List<Puzzle>>(emptyList()) }
    var puzzleProgressList by remember { mutableStateOf<List<PuzzleProgress>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    val homeUiState by homeViewModel.homeState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> homeViewModel.pauseMusic()
                    Lifecycle.Event.ON_RESUME -> homeViewModel.resumeMusic()
                    else -> {}
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        puzzles = loadPuzzlesFromJson(context)
        puzzles.forEach { puzzle ->
            val puzzleProgress = homeViewModel.getPuzzleProgress(puzzle.id)

            if (puzzleProgress != null) {
                puzzleProgressList = puzzleProgressList + puzzleProgress
            } else {
                puzzleProgressList =
                    puzzleProgressList + PuzzleProgress(puzzle.id, 0, puzzle.parts.size)
            }
        }
//        println("puzzles: $puzzleProgressList")
    }

    Scaffold(
        topBar = {
            TopBar(coins = 100) {
                showDialog = true
            }
        },
    ) {
        HomeScreenContent(
            modifier = Modifier.padding(it),
            navigateToGameScreen = { navController.navigate("game/$it") },
            puzzleProgressList = puzzleProgressList,
        )
    }

    if (showDialog) {
        SettingsDialog(
            onDismiss = { showDialog = false },
            onSoundToggle = { homeViewModel.toggleSound() },
            onMusicToggle = { homeViewModel.toggleMusic() },
            onChangeBackground = { homeViewModel.changeBackground("default") },
            uiState = homeUiState,
        )
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navigateToGameScreen: (Int) -> Unit,
    puzzleProgressList: List<PuzzleProgress>,
) {
    var showDialog by remember { mutableStateOf(false) }
    var lockedPuzzleMessage by remember { mutableStateOf("") }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PuzzleSelectionScreen(
                puzzleProgressList = puzzleProgressList,
                onPuzzleClick = { puzzleId, isLocked ->

                    Log.d("HomeScreen", "onPuzzleClick: puzzleId=$puzzleId, isLocked=$isLocked")

                    if (!isLocked) {
                        navigateToGameScreen(puzzleId)
                    } else {
                        showDialog = true
                        lockedPuzzleMessage =
                            "Puzzle $puzzleId is locked. Complete the previous puzzle to unlock"
                    }
                },
            )
        }
    }
    if (showDialog) {
        LockedPuzzleDialog(
            message = lockedPuzzleMessage,
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
fun LockedPuzzleDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Puzzle Locked") },
        text = { Text(text = message) },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
            ) {
                Text("OK")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    coins: Int,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Coin icon and number of coins
                Row(
                    modifier =
                        Modifier
                            .background(Color.Blue.copy(0.5f), shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_monetization_on),
                        contentDescription = "Coins",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Yellow,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$coins")
                }

                // Settings Icon
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                    )
                }
            }
        },
    )
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onMusicToggle: (Boolean) -> Unit,
    onChangeBackground: () -> Unit,
    uiState: HomeState,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Settings")
        },
        text = {
            Column {
                // Sound option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Sound")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = uiState.soundEnabled,
                        onCheckedChange = { onSoundToggle(it) },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Music option
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Music")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = uiState.musicEnabled,
                        onCheckedChange = { onMusicToggle(it) },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Background change option
                Button(onClick = {
                    onChangeBackground()
                    onDismiss()
                }) {
                    Text(text = "Change Background")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Close")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TopBar(coins = 100) {
    }

//    HomeScreenContent(
//        navigateToGameScreen = {},
//        puzzleProgressList = listOf(
//            PuzzleProgress(puzzleId = 1, completedParts = 2, totalParts = 5),
//            PuzzleProgress(puzzleId = 2, completedParts = 3, totalParts = 5),
//            PuzzleProgress(puzzleId = 3, completedParts = 0, totalParts = 5),
//        ),
//    )
}
