package com.noukta.wallpaper.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noukta.wallpaper.R
import com.noukta.wallpaper.ui.nav.Screen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppTopBar(
    screen: Screen,
    query: String,
    updateQuery: (String) -> Unit,
    searchByTag: (String) -> Unit,
    onLogoClick : () -> Unit,
    modifier: Modifier = Modifier) {
    val animator = remember {
        Animatable(0f)
    }
    when(screen){
        Screen.Home, Screen.Search ->{
            val keyboardController = LocalSoftwareKeyboardController.current
            LaunchedEffect(Unit) {
                if(animator.value != 1f) {
                    animator.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 3000,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
            TopAppBar(
                title = {
                    if(animator.value > 0f){
                        IconButton(
                            onClick = onLogoClick,
                            enabled = screen != Screen.Home
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_topbar_foreground),
                                contentDescription = null,
                                modifier = Modifier
                                    .graphicsLayer(
                                        scaleX = animator.value,
                                        scaleY = animator.value,
                                        alpha = animator.value,
                                        rotationZ = -360f * (1 - animator.value)
                                    ),
                                tint = Color.Unspecified
                            )
                        }
                    }
                },
                modifier = modifier,
                actions = {
                    SearchBar(
                        query = query,
                        onQueryChange = {
                            updateQuery(it)
                        },
                        onSearch = {
                            searchByTag(it)
                            keyboardController?.hide()
                        },
                        active = false,
                        onActiveChange = {
                        },
                        modifier = Modifier.fillMaxWidth(1f - .2f * animator.value),
                        placeholder = {
                            if(animator.value == 1f)
                                Text(stringResource( R.string.search_wallpapers))
                            else{
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer(
                                            scaleX = 1 - animator.value,
                                            scaleY = 1 - animator.value,
                                            alpha = 1 - animator.value
                                        ),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_topbar_foreground),
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 5.dp),
                                        tint = Color.Unspecified
                                    )
                                    Text(stringResource( R.string.app_name))
                                }
                            }
                        },
                        trailingIcon = {
                            if(animator.value == 1f)
                                IconButton(onClick = {
                                    searchByTag(query)
                                    keyboardController?.hide()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null
                                    )
                                }
                        }
                    ) {

                    }
                }
            )
        }
        Screen.Favorites ->{
            TopAppBar(
                title = {
                    Text(
                        stringResource(screen.titleRes!!)
                    )
                },
                modifier = modifier
            )
        }
        else ->{

        }
    }
}