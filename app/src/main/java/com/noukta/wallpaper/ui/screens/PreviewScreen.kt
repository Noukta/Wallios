package com.noukta.wallpaper.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R
import com.noukta.wallpaper.admob.AdmobHelper
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ext.shareWallpaper
import com.noukta.wallpaper.settings.AdUnit.INTERSTITIAL
import com.noukta.wallpaper.ui.components.ListDialog
import com.noukta.wallpaper.ui.theme.favorite_color
import com.noukta.wallpaper.util.ImageHelper
import com.noukta.wallpaper.util.WallpaperWorker
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewScreen(
    wallpapers: List<Wallpaper>,
    initialWallpaper: Int,
    onLikeClick: (Wallpaper, Boolean) -> Unit,
    onTagClick: (Category) -> Unit,
    isFavorite: suspend (String) -> Boolean
) {
    // Safety check for empty list
    if (wallpapers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val safeInitialWallpaper = initialWallpaper.coerceIn(0, wallpapers.lastIndex)
    val pagerState = rememberPagerState(safeInitialWallpaper)
    val currentWallpaper by remember {
        derivedStateOf {
            wallpapers.getOrNull(pagerState.currentPage) ?: wallpapers.first()
        }
    }

    var liked by remember {
        mutableStateOf(false)
    }
    var showModeSelection by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(pagerState.currentPage) {
        liked = withContext(Dispatchers.IO) {
            isFavorite(currentWallpaper.id)
        }
    }

    // Preload interstitial ad when screen loads for better UX
    LaunchedEffect(Unit) {
        AdmobHelper.loadInterstitial(context, INTERSTITIAL)
    }

    Box(modifier = Modifier.fillMaxSize()){
        VerticalPager(
            pageCount = wallpapers.size,
            state = pagerState
        ) {
            CoilImage(
                imageModel = { wallpapers[it].url },
                modifier = Modifier.fillMaxSize(),
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                )
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
                ElevatedAssistChip(
                    onClick = {
                              onTagClick(currentWallpaper.category)
                    },
                    label = { Text(text = currentWallpaper.category.name) },
                    shape = RoundedCornerShape(50),
                    colors = AssistChipDefaults.elevatedAssistChipColors(
                        containerColor = currentWallpaper.category.color,
                        labelColor = Color.Black
                    )
                )
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
                    onClick = {
                        onLikeClick(currentWallpaper, liked)
                        liked = !liked
                    }
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (liked) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
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
                    onClick = {
                        ImageHelper.urlToBitmap(
                            imageURL = currentWallpaper.url,
                            context = context
                        ){
                            shareWallpaper(context, it)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_wallpaper)
                    )
                }
            }

            SnackbarHost(hostState = snackState)
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
            showModeSelection = false
            WallpaperWorker.setWallpaper(context, currentWallpaper.url, it){
                AdmobHelper.showInterstitial(context, INTERSTITIAL)
            }
        }
    }
}