package com.noukta.wallpaper.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.noukta.wallpaper.R
import com.noukta.wallpaper.admob.AppOpenAdManager
import com.noukta.wallpaper.settings.AdUnit
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onLoadFinished: () -> Unit) {
    val context = LocalContext.current
    val appOpenAdManager = AppOpenAdManager()

    LaunchedEffect(Unit) {
        for (i in AdUnit.APP_OPEN_DELAY * 2 downTo 0) {
            delay(500)
            /*appOpenAdManager.showAdIfAvailable(
                context as Activity,
                AdUnit.APP_OPEN
            ) {
                onLoadFinished()
            }*/
        }
        onLoadFinished()
    }

    val animator = remember {
        Animatable(0f)
    }
    LaunchedEffect(Unit) {
        animator.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = AdUnit.APP_OPEN_DELAY * 1000
            )
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_screen),
            contentDescription = null,
            modifier = Modifier
                .wrapContentSize()
                .graphicsLayer(
                    alpha = 1f - animator.value,
                    rotationZ = 360f * animator.value
                )
        )
    }
}