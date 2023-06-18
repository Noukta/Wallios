package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    refreshable: Boolean = false,
    onRefresh: () -> Unit = {},
    onWallpaperPreview: (wallpaperIdx: Int) -> Unit
) {
    val listState = rememberLazyGridState()

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    val refreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshScope.launch {
            refreshing = true
            delay(1000)
            onRefresh()
            refreshing = false
        }
    })

    val enableScrollUp by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0
        }
    }

    var scrollUp by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(scrollUp) {
        scrollUp = false
        listState.animateScrollToItem(0)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .pullRefresh(refreshState, refreshable)
    ) {
        itemsIndexed(wallpapers) { index, wallpaper ->
            WallpaperThumbnail(
                wallpaper = wallpaper,
                onLikeClick = { liked ->
                    onLikeClick(wallpaper, liked)
                }
            ) {
                onWallpaperPreview(index)
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

    if (enableScrollUp) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                modifier = Modifier.padding(32.dp),
                onClick = { scrollUp = true }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null
                )
            }
        }
    }
}