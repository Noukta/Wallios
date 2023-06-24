package com.noukta.wallpaper

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ui.UiState
import com.noukta.wallpaper.util.DataScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var searchTag by mutableStateOf("")

    var wallpaperIdx by mutableStateOf(0)
        private set
    var showExit by mutableStateOf(false)
    var showReview by mutableStateOf(false)

    fun fetchWallpapers() {
        _uiState.value.wallpapers.clear()
        val db = Firebase.firestore
        db.collection("wallpapers")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tags: List<String> = document.get("tags") as List<String>
                    val wallpaper = Wallpaper(
                        id = document.id,
                        url = document.data["url"] as String,
                        category = Category.valueOf(tags[0].replaceFirstChar {it.titlecase()}),
                        tags = tags
                    )
                    _uiState.value.wallpapers.add(wallpaper)
                }
                shuffleWallpapers()
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }
    }

    fun shuffleWallpapers() {
        _uiState.value.wallpapers.shuffle()
    }

    fun updateWallpaperIdx(index: Int, lastIndex: Int) {
        wallpaperIdx = index.coerceAtMost(lastIndex)
    }

    fun fetchFavorites() {
        DataScope.launch {
            _uiState.value.favorites.clear()
            _uiState.value.favorites.addAll(DatabaseHolder.Database.favoritesDao().getAll())
            for (i in 0.._uiState.value.favorites.lastIndex){
                val id  = _uiState.value.favorites[i].id
                _uiState.value.favorites[i] = _uiState.value.wallpapers.find { it.id == id }!!
            }
        }
    }

    fun searchByText(text: String){
        DataScope.launch {
            _uiState.value.wallpapers.forEach {
                it.match(text)
            }
            _uiState.value.searchResult.clear()
            _uiState.value.searchResult.addAll(
                _uiState.value.wallpapers
                    .filter { it.relevance > 0 }
                    .sortedByDescending { it.relevance }
            )
        }
    }

    /**
     * add wallpaper to favorites when it's already liked or remove from favorites.
     * @param liked true means already liked (false by default)
     */
    fun likeWallpaper(wallpaper: Wallpaper, liked: Boolean = false) {
        DataScope.launch {
            if (liked) DatabaseHolder.Database.favoritesDao().delete(wallpaper)
            else DatabaseHolder.Database.favoritesDao().insertAll(wallpaper)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        (owner as MainActivity).onBackPressedDispatcher
            .addCallback(owner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExit = !showExit
                }
            })
    }
}
