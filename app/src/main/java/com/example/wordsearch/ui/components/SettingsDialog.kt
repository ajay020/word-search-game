import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.wordsearch.R

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    isSoundEnabled: Boolean,
    onSoundToggle: (Boolean) -> Unit,
    isMusicEnabled: Boolean,
    onMusicToggle: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = { },
        properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .clip(shape = RoundedCornerShape(16.dp))
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.Blue),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    "Settings",
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close button",
                        tint = Color.White,
                    )
                }
            }
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconWithText(
                        icon = R.drawable.ic_music,
                        text = "Sound",
                        modifier = Modifier.padding(0.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = onSoundToggle,
                        colors =
                            SwitchDefaults.colors(
                                checkedTrackColor = Color.Blue,
                                checkedThumbColor = Color.White,
                                disabledCheckedThumbColor = Color.LightGray
                            ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconWithText(
                        icon = R.drawable.ic_volume,
                        text = "Music",
                        modifier = Modifier.padding(0.dp),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isMusicEnabled,
                        onCheckedChange = onMusicToggle,
                        colors =
                        SwitchDefaults.colors(
                            checkedTrackColor = Color.Blue,
                            checkedThumbColor = Color.White,
                            disabledCheckedThumbColor = Color.LightGray
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier =
                        Modifier
                            .clickable {
                                openPrivacyPolicy(context)
                            }.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconWithText(
                        icon = R.drawable.ic_privacy,
                        text = "Privacy Policy",
                        modifier = Modifier.padding(0.dp),
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier.padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "arrow icon",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconWithText(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    iconTint: Color = Color.Black,
    textColor: Color = Color.Black,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconTint,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = textColor,
        )
    }
}

fun openPrivacyPolicy(context: Context) {
    val privacyPolicyUrl = "https://sites.google.com/view/wordsearchfun-privacy-policy"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
    context.startActivity(intent)
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 619,
)
@Composable
private fun ExitConfirmDialogPreview() {
    SettingsDialog(
        onDismiss = {},
        isSoundEnabled = false,
        onSoundToggle = {},
        isMusicEnabled = true,
        onMusicToggle = {},
    )
}
