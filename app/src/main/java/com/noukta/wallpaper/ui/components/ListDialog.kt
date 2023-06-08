package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R

@Composable
fun ListDialog(
    items: List<String>,
    onDismissRequest: () -> Unit,
    onClick: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ){
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                items.forEachIndexed { index, title ->
                    Card(
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(30.dp)
                            )
                            .clickable {
                                onClick(index)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )

                    ) {
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        )
                    }
                }
            }
        }
    )
}