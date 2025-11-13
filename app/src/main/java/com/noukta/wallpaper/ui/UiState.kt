package com.noukta.wallpaper.ui

import com.noukta.wallpaper.db.obj.Wallpaper

data class UiState(
    val wallpapers: List<Wallpaper> = emptyList(),
    val favorites: List<Wallpaper> = emptyList(),
    val searchResult: List<Wallpaper> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)