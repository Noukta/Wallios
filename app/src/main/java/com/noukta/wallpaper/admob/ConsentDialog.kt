package com.noukta.wallpaper.admob

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.noukta.wallpaper.R
import com.noukta.wallpaper.settings.URL
import com.noukta.wallpaper.ui.theme.WallpaperAppTheme

@Composable
fun ConsentDialog(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    )
    AlertDialog(
        onDismissRequest = {},
        modifier = modifier,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.accept)
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.ready)
            )
        },
        text = {
            Column {
                val consentText = buildAnnotatedString {
                    // Add some text
                    append(context.getString(R.string.consent))

                    // Add a link
                    pushStringAnnotation(tag = "link", annotation = URL.PRIVACY_POLICY)
                    append("privacy policy.")
                    pop()
                }
                Text(consentText)
            }

        },
        properties = properties
    )
}

@Preview(showBackground = true)
@Composable
fun ConsentDialogPreview() {
    WallpaperAppTheme {
        ConsentDialog({}
        )
    }
}