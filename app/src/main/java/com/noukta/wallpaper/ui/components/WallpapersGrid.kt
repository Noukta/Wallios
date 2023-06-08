package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.db.obj.Wallpaper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WallpapersGrid(
    wallpapers: List<Wallpaper>,
    listState: LazyGridState,
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    onShuffle: () -> Unit,
    onWallpaperPreview: (wallpaperIdx: Int) -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    val refreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshScope.launch {
            refreshing = true
            delay(1000)
            onShuffle()
            refreshing = false
        }
    })

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .pullRefresh(refreshState)
    ) {
        itemsIndexed(wallpapers) { index, wallpaper ->
            WallpaperThumbnail(
                wallpaper = wallpaper,
                onLikeClick = { liked ->
                    onLikeClick(wallpaper, liked)
                }
            ) {
                onWallpaperPreview(
                    index
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            scale = true
        )
    }
}