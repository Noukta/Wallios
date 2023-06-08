package com.noukta.wallpaper.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.theme.WallpaperAppTheme
import com.noukta.wallpaper.ui.theme.favorite_color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WallpaperThumbnail(
    index: Int, wallpaper: Wallpaper, onLikeClick: (Boolean) -> Unit, onClick: () -> Unit
) {
    var liked by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            liked = DatabaseHolder.Database.favoritesDao().exists(wallpaper.id)
        }
    }
    ElevatedCard(
        modifier = Modifier
            .height(320.dp)
            .padding(10.dp),
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
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                //MaterialTheme.colorScheme.background.copy(alpha = .15f),
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
                    },
                    //modifier = Modifier.align(Alignment.CenterEnd)
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

@Preview(
    showBackground = true,
    device = "spec:width=180dp,height=320dp,dpi=440",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_UNDEFINED,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
fun WallpaperThumbnailPreview() {
    WallpaperAppTheme {
        WallpaperThumbnail(
            index = 0,
            wallpaper = Wallpaper(R.raw.wallpaper_0013, categories = listOf(Category.Movie)),
            onLikeClick = {}) {

        }
    }
}