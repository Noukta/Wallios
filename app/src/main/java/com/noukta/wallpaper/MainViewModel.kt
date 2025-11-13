package com.noukta.wallpaper

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.dao.FavoritesDao
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val favoritesDao: FavoritesDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var searchTag by mutableStateOf("")

    var wallpaperIdx by mutableStateOf(0)
        private set
    var showExit by mutableStateOf(false)
        private set
    var showReview by mutableStateOf(false)

    companion object {
        private const val WALLPAPERS_PAGE_SIZE = 50L
    }

    fun fetchWallpapers(limit: Long = WALLPAPERS_PAGE_SIZE) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        firestore.collection("wallpapers")
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                val wallpaperList = mutableListOf<Wallpaper>()
                for (document in result) {
                    val tags: List<String> = document.get("tags") as? List<String> ?: emptyList()
                    if (tags.isNotEmpty()) {
                        val wallpaper = Wallpaper(
                            id = document.id,
                            url = document.data["url"] as? String ?: "",
                            category = Category.valueOf(tags[0].replaceFirstChar {it.titlecase(Locale.getDefault())}),
                            tags = tags
                        )
                        wallpaperList.add(wallpaper)
                    }
                }
                _uiState.update { it.copy(wallpapers = wallpaperList.shuffled(), isLoading = false) }
                Log.d("Firestore", "Loaded ${wallpaperList.size} wallpapers")
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
                _uiState.update { it.copy(error = exception.message, isLoading = false) }
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
            favoritesDao.getAll().collect { favoriteIds ->
                val favoriteWallpapers = favoriteIds.mapNotNull { favorite ->
                    _uiState.value.wallpapers.find { it.id == favorite.id }
                }
                _uiState.update { it.copy(favorites = favoriteWallpapers) }
            }
        }
    }

    fun searchByText(text: String){
        viewModelScope.launch(Dispatchers.Default) {
            val searchResults = _uiState.value.wallpapers
                .map { wallpaper ->
                    wallpaper.apply { match(text) }
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
            if (liked) favoritesDao.delete(wallpaper)
            else favoritesDao.insertAll(wallpaper)
        }
    }

    fun toggleExitDialog() {
        showExit = !showExit
    }
}
