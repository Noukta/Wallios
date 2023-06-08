package com.noukta.wallpaper.ui.screens

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.components.WallpapersGrid


@Composable
fun HomeScreen(
    wallpapers: List<Wallpaper>,
    firstVisibleIdx: Int,
    firstVisibleOffset: Int,
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    onShuffle: () -> Unit,
    onWallpaperPreview: (wallpaperIdx: Int, firstVisibleIdx: Int, firstVisibleOffset: Int) -> Unit
) {

    val listState = rememberLazyGridState(
        initialFirstVisibleItemIndex = firstVisibleIdx,
        initialFirstVisibleItemScrollOffset = firstVisibleOffset
    )

    WallpapersGrid(wallpapers = wallpapers,
        listState = listState,
        onLikeClick = { wallpaper, liked ->
            onLikeClick(wallpaper, liked)
        },
        onShuffle = onShuffle,
        onWallpaperPreview = { _wallpaperIdx, _firstVisibleIdx, _firstVisibleOffset ->
            onWallpaperPreview(_wallpaperIdx, _firstVisibleIdx, _firstVisibleOffset)
        })
}