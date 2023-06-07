package com.noukta.wallpaper.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.noukta.wallpaper.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int? = null,
    val icon: ImageVector? = null,
    val barHidden: Boolean = false
) {
    companion object {
        val items = listOf(
            Splash,
            Home,
            Favorites,
            Preview
        )
    }

    object Splash : Screen(
        route = "splash",
        barHidden = true
    )

    object Home : Screen(
        route = "home",
        titleRes = R.string.home,
        icon = Icons.Default.Home
    )

    object Favorites : Screen(
        route = "favorites",
        titleRes = R.string.favorites,
        icon = Icons.Default.Favorite
    )

    object Preview : Screen(
        route = "preview",
        barHidden = true
    )
}