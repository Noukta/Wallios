package com.noukta.wallpaper.ui.components

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noukta.wallpaper.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(screen: Screen, modifier: Modifier = Modifier) {
    Log.d("nav", "${screen.titleRes}")
    if (screen in listOf(Screen.Home, Screen.Favorites)) {
        TopAppBar(
            title = { Text(stringResource(screen.titleRes!!)) },
            modifier = modifier,
            actions = {
                // TODO: search and menu button
            }
        )
    }
}