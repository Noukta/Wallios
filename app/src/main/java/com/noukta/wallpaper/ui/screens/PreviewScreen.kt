package com.noukta.wallpaper.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.util.DataScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewScreen(
    wallpapers: List<Wallpaper>,
    initialWallpaper: Int,
    onLikeClick:  (Wallpaper, Boolean) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialWallpaper)
    var liked by remember {
        mutableStateOf(true)
    }
    var showModeSelection by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(pagerState.currentPage) {
        DataScope.launch {
            liked = DatabaseHolder.Database.favoritesDao().exists(wallpapers[pagerState.currentPage].id)
        }
    }
}