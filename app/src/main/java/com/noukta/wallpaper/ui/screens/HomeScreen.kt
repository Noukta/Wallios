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
    WallpapersGrid(wallpapers = wallpapers,
        onLikeClick = { wallpaper, liked ->
            onLikeClick(wallpaper, liked)
        },
        refreshable = true,
        onRefresh = onShuffle,
        onWallpaperPreview = { _wallpaperIdx ->
            onWallpaperPreview(_wallpaperIdx)
        }
    )
}