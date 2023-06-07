package com.noukta.wallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.noukta.wallpaper.ui.MainContent
import com.noukta.wallpaper.ui.theme.WallpaperAppTheme

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WallpaperAppTheme {
                // A surface container using the 'background' color from the theme
                MainContent(vm)
            }
        }
        lifecycle.addObserver(vm)
    }
}