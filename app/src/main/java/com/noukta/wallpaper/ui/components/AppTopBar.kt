package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R
import com.noukta.wallpaper.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(screen: Screen, modifier: Modifier = Modifier) {
    if (screen in listOf(Screen.Home, Screen.Favorites)) {
        TopAppBar(
            title = {
                Row() {
                    if(screen == Screen.Home)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground_hdpi),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 5.dp),
                            tint = Color.Unspecified
                        )
                    Text(stringResource(
                        when(screen){
                            Screen.Home -> R.string.app_name
                            else -> screen.titleRes!!
                        }
                    ))
                }
            },
            modifier = modifier,
            actions = {
                // TODO: search and menu button
            }
        )
    }
}