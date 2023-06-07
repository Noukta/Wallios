package com.noukta.wallpaper.ui.components

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R

@Composable
fun ExitDialog(show: Boolean, onDismiss: () -> Unit) {
    val activity = LocalContext.current as Activity

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.exit_title)) },
            text = { Text(text = stringResource(R.string.exit_text)) },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text(stringResource(R.string.no))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        activity.finish()
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            }
        )
    }
}