package com.noukta.wallpaper.ui.screens

import androidx.compose.runtime.Composable
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.components.WallpapersGrid


@Composable
fun HomeScreen(
    wallpapers: List<Wallpaper>,
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    onShuffle: () -> Unit,
    onWallpaperPreview: (wallpaperIdx: Int) -> Unit
) {
    WallpapersGrid(
        wallpapers = wallpapers,
        onLikeClick = onLikeClick,
        refreshable = true,
        onRefresh = onShuffle,
        onWallpaperPreview = onWallpaperPreview
    )
}