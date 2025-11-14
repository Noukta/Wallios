package com.noukta.wallpaper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.theme.favorite_color
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WallpaperThumbnail(
    wallpaper: Wallpaper, onLikeClick: (Boolean) -> Unit, onClick: () -> Unit
) {
    val context = LocalContext.current
    var liked by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            liked = DatabaseHolder.database.favoritesDao().exists(wallpaper.id)
        }
    }
    ElevatedCard(
        modifier = Modifier
            .height(320.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(25)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStartPercent = 5, bottomEndPercent = 5))
                    .clickable { onClick() },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                ),
                imageRequest = {
                    ImageRequest.Builder(context)
                        .data(wallpaper.url)
                        .crossfade(true)
                        .build() },
                imageLoader = {
                    ImageLoader.Builder(context)
                        .memoryCache { MemoryCache.Builder(context).maxSizePercent(0.25).build() }
                        .crossfade(true)
                        .build() }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = .45f)
                            )
                        )
                    )
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(
                    onClick = {
                        onLikeClick(liked)
                        liked = !liked
                    }
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
            }
        }
    }
}