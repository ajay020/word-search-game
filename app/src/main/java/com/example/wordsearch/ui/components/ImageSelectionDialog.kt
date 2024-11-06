package com.example.wordsearch.ui.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.wordsearch.R
import com.example.wordsearch.ui.theme.WordSearchTheme

val images =
    listOf(
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4,
        R.drawable.bg5,
        R.drawable.bg6,
        R.drawable.bg7,
        R.drawable.bg8,
        R.drawable.stars,
        R.drawable.rainy,
        R.drawable.mountain,
    )

@Composable
fun ThemeSelectionDialog(
    onImageSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
    ) {
        Column(
            modifier =
                Modifier
                    .background(Color.LightGray)
                    .padding(0.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Choose a Background",
                    color = Color.Black,
                )
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close button",
                        tint = Color.Black,
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                items(images) { imgRes ->
                    Image(
                        painter = painterResource(id = imgRes),
                        contentDescription = "Background Image",
                        contentScale = ContentScale.FillBounds,
                        modifier =
                            Modifier
                                .padding(6.dp)
                                .size(140.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onImageSelected(imgRes) },
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 370,
    heightDp = 700,
)
@Composable
private fun ImageSelectionDialogPreview() {

        ThemeSelectionDialog(
            onDismiss = {},
            onImageSelected = {},
        )
}
