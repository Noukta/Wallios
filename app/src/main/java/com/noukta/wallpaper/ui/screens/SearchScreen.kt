package com.noukta.wallpaper.ui.screens

import androidx.compose.runtime.Composable
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.components.WallpapersGrid


@Composable
fun SearchScreen(
    wallpapers: List<Wallpaper>,
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    onWallpaperPreview: (wallpaperIdx: Int) -> Unit,
    isFavorite: suspend (String) -> Boolean
) {
    WallpapersGrid(
        wallpapers = wallpapers,
        onLikeClick = onLikeClick,
        onWallpaperPreview = onWallpaperPreview,
        isFavorite = isFavorite
    )
}