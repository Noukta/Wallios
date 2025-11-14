package com.noukta.wallpaper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.repository.WallpaperRepository
import com.noukta.wallpaper.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var searchTag by mutableStateOf("")

    var wallpaperIdx by mutableStateOf(0)
        private set
    var showExit by mutableStateOf(false)
        private set
    var showReview by mutableStateOf(false)

    private var isAuthReady by mutableStateOf(false)
    private var pendingDataFetch = false

    companion object {
        private const val WALLPAPERS_PAGE_SIZE = 50L
    }

    fun onAuthSuccess() {
        isAuthReady = true
        if (pendingDataFetch && _uiState.value.wallpapers.isEmpty()) {
            fetchWallpapers()
        }
    }

    fun fetchWallpapers(limit: Long = WALLPAPERS_PAGE_SIZE) {
        if (!isAuthReady) {
            pendingDataFetch = true
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.fetchWallpapers(limit).collect { result ->
                result.fold(
                    onSuccess = { wallpapers ->
                        _uiState.update { it.copy(wallpapers = wallpapers.shuffled(), isLoading = false) }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(error = exception.message, isLoading = false) }
                    }
                )
            }
        }
    }

    fun shuffleWallpapers() {
        _uiState.update { it.copy(wallpapers = it.wallpapers.shuffled()) }
    }

    fun updateWallpaperIdx(index: Int, lastIndex: Int) {
        wallpaperIdx = index.coerceAtMost(lastIndex)
    }

    fun fetchFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavorites().collect { favoriteWallpapers ->
                _uiState.update { it.copy(favorites = favoriteWallpapers) }
            }
        }
    }

    fun searchByText(text: String){
        viewModelScope.launch(Dispatchers.Default) {
            val searchResults = _uiState.value.wallpapers
                .map { wallpaper ->
                    wallpaper.match(text)
                }
                .filter { it.relevance > 0 }
                .sortedByDescending { it.relevance }
            _uiState.update { it.copy(searchResult = searchResults) }
        }
    }

    /**
     * add wallpaper to favorites when it's already liked or remove from favorites.
     * @param liked true means already liked (false by default)
     */
    fun likeWallpaper(wallpaper: Wallpaper, liked: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (liked) repository.removeFromFavorites(wallpaper)
            else repository.addToFavorites(wallpaper)
        }
    }

    fun toggleExitDialog() {
        showExit = !showExit
    }
}
