package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.db.obj.Wallpaper

@Composable
fun WallpapersGrid(
    wallpapers: List<Wallpaper>,
    listState: LazyGridState,
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    onShuffle: () -> Unit,
    onWallpaperPreview: (wallpaperIdx: Int, firstVisibleIdx: Int, firstVisibleOffset: Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        itemsIndexed(wallpapers) { index, wallpaper ->
            WallpaperThumbnail(
                index = index, wallpaper = wallpaper,
                onLikeClick = { liked ->
                    onLikeClick(wallpaper, liked)
                }
            ) {
                onWallpaperPreview(
                    index,
                    listState.firstVisibleItemIndex,
                    listState.firstVisibleItemScrollOffset
                )
            }
        }
    }
}