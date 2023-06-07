package com.noukta.wallpaper

import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.noukta.wallpaper.data.dummyWallpapers
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.UiState
import com.noukta.wallpaper.util.IoScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    var wallpaperIdx by mutableStateOf(0)
        private set
    var homeFirstVisibleOffset by mutableStateOf(0)
        private set
    var homeFirstVisibleIdx by mutableStateOf(0)
        private set

    var favoriteIdx by mutableStateOf(0)
        private set
    var favoritesFirstVisibleOffset by mutableStateOf(0)
        private set
    var favoritesFirstVisibleIdx by mutableStateOf(0)
        private set

    var showExit by mutableStateOf(false)
    var showReview by mutableStateOf(false)

    //Home Screen Logic
    fun fetchWallpapers() {
        // TODO: fetch wallpapers
        _uiState.value.wallpapers.addAll(dummyWallpapers)
        shuffleWallpapers()
    }

    fun shuffleWallpapers() {
        _uiState.value.wallpapers.shuffle()
    }

    fun updateWallpaperIdx(index: Int = 0) {
        wallpaperIdx = index.coerceAtMost(_uiState.value.wallpapers.lastIndex)
    }

    fun persistHomeScreen(index: Int, offset: Int) {
        homeFirstVisibleIdx = index
        homeFirstVisibleOffset = offset
    }

    //Favorites Screen Logic
    fun fetchFavorites() {
        IoScope.launch {
            _uiState.value.favorites.addAll(DatabaseHolder.Database.favoritesDao().getAll())
        }
    }

    fun updateFavoriteIdx(index: Int = 0) {
        favoriteIdx = index.coerceAtMost(_uiState.value.favorites.lastIndex)
    }

    fun persistFavoritesScreen(index: Int, offset: Int) {
        favoritesFirstVisibleIdx = index
        favoritesFirstVisibleOffset = offset
    }

    /**
     * add wallpaper to favorites when it's already liked or remove from favorites.
     * @param liked true means already liked (false by default)
     */
    fun likeWallpaper(wallpaper: Wallpaper, liked: Boolean = false) {
        IoScope.launch {
            if (liked) {
                DatabaseHolder.Database.favoritesDao().delete(wallpaper)
                _uiState.value.favorites.remove(wallpaper)
            } else {
                DatabaseHolder.Database.favoritesDao().insertAll(wallpaper)
                _uiState.value.favorites.add(wallpaper)
            }
        }
    }

    //MainActivity Lifecycle Observing
    override fun onCreate(owner: LifecycleOwner) {
        (owner as MainActivity).onBackPressedDispatcher
            .addCallback(owner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExit = !showExit
                }
            })
    }
}
