package com.noukta.wallpaper.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.DatabaseHolder.Companion.Database
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.theme.WallpaperAppTheme
import com.noukta.wallpaper.ui.theme.favorite_color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
            Log.d("state", "$index: $wallpaper")
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

@Composable
fun WallpaperThumbnail(
    index: Int, wallpaper: Wallpaper, onLikeClick: (Boolean) -> Unit, onClick: () -> Unit
) {
    var liked by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            liked = Database.favoritesDao().exists(wallpaper.id)
        }
    }
    ElevatedCard(
        modifier = Modifier
            .height(320.dp)
            .padding(10.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSystemInDarkTheme()) wallpaper.categories[0].darkColor else wallpaper.categories[0].lightColor
        ),
        shape = RoundedCornerShape(15)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = wallpaper.id),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5))
                    .clickable { onClick() },
                contentScale = ContentScale.Crop
            )
            val catColor =
                if (isSystemInDarkTheme()) wallpaper.categories[0].darkColor else wallpaper.categories[0].lightColor
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent, catColor
                            )
                        )
                    )
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = {
                        onLikeClick(liked)
                        liked = !liked
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = when {
                            liked -> favorite_color
                            isSystemInDarkTheme() -> Color.White
                            else -> Color.Black
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=180dp,height=320dp,dpi=440"
)
@Composable
fun WallpaperThumbnailPreview() {
    WallpaperAppTheme {
        WallpaperThumbnail(
            index = 0,
            wallpaper = Wallpaper(R.raw.wallpaper_0000, categories = listOf(Category.Movie)),
            onLikeClick = {}) {

        }
    }
}