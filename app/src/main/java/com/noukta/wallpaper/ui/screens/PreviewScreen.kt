package com.noukta.wallpaper.ui.screens

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.noukta.wallpaper.R
import com.noukta.wallpaper.admob.AdmobHelper
import com.noukta.wallpaper.data.dummyWallpapers
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ext.shareWallpaper
import com.noukta.wallpaper.settings.AdUnit.INTERSTITIAL
import com.noukta.wallpaper.ui.components.ListDialog
import com.noukta.wallpaper.ui.theme.WallpaperAppTheme
import com.noukta.wallpaper.ui.theme.favorite_color
import com.noukta.wallpaper.util.DataScope
import com.noukta.wallpaper.util.MODE
import com.noukta.wallpaper.util.WALLPAPER_ID
import com.noukta.wallpaper.util.WallpaperWorker

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewScreen(
    wallpapers: List<Wallpaper>,
    initialWallpaper: Int,
    onLikeClick:  (Wallpaper, Boolean) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialWallpaper)
    val currentWallpaper by remember {
        derivedStateOf {
            wallpapers[pagerState.currentPage]
        }
    }

    var liked by remember {
        mutableStateOf(true)
    }
    var showModeSelection by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(pagerState.currentPage) {
        DataScope.launch {
            liked = DatabaseHolder.Database.favoritesDao().exists(currentWallpaper.id)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        VerticalPager(
            pageCount = wallpapers.size,
            state = pagerState
        ) {
            Image(
                painter = painterResource(id = wallpapers[it].id),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.TopStart
        ){
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentWallpaper.categories.forEach {
                    ElevatedAssistChip(
                        onClick = {},
                        label = { Text(text = it.name) },
                        shape = RoundedCornerShape(50),
                        colors = AssistChipDefaults.elevatedAssistChipColors(
                            containerColor = if(isSystemInDarkTheme()) it.darkColor else it.lightColor
                        )
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ){
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                FilledTonalIconButton(
                    onClick = { onLikeClick(currentWallpaper, liked) }
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = when {
                            liked -> favorite_color
                            else -> MaterialTheme.colorScheme.onBackground
                        }
                    )
                }
                FilledTonalButton(onClick = { showModeSelection = true }) {
                    Text(text = stringResource(R.string.set_wallpaper))
                }
                FilledTonalIconButton(
                    onClick = { shareWallpaper(context, currentWallpaper.id) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
            }
        }
    }

    //Set wallpaper dialog
    if (showModeSelection) {
        ListDialog(
            items = listOf(
                stringResource(R.string.both),
                stringResource(R.string.home),
                stringResource(R.string.lockscreen)
            ),
            onDismissRequest = {
                showModeSelection = false
            }
        ) {
            val workManager = WorkManager.getInstance(context)
            val data = Data.Builder()
                .putInt(WALLPAPER_ID, currentWallpaper.id)
                .putInt(MODE, it)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<WallpaperWorker>()
                .setInputData(data)
                .build()
            workManager.enqueue(workRequest)
            showModeSelection = false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2)
                AdmobHelper.showInterstitial(context, INTERSTITIAL)
        }
    }
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewScreenPreview() {
    WallpaperAppTheme {
        PreviewScreen(
            wallpapers = dummyWallpapers,
            initialWallpaper = 10,
            onLikeClick = {_, _ -> }
        )
    }
}