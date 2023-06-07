package com.noukta.wallpaper.ui

import com.noukta.wallpaper.db.obj.Wallpaper

data class UiState(
    val wallpapers: MutableList<Wallpaper> = mutableListOf(),
    val favorites: MutableList<Wallpaper> = mutableListOf()
)