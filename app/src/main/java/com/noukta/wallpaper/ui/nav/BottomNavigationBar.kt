package com.noukta.wallpaper.ui.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun BottomNavigationBar(screen: Screen, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (screen in listOf(Screen.Home, Screen.Favorites)) {
        NavigationBar(modifier = modifier) {
            Screen.items.filter { !it.barHidden }.forEach {
                NavigationBarItem(
                    icon = { Icon(it.icon!!, null) },
                    label = { Text(stringResource(it.titleRes!!)) },
                    selected = screen == it,
                    enabled = screen != it,
                    onClick = onClick
                )
            }
        }
    }
}