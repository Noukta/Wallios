package com.noukta.wallpaper

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.DatabaseHolder
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ext.requestNotificationsPermission
import com.noukta.wallpaper.ui.UiState
import com.noukta.wallpaper.util.PrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel : ViewModel(), DefaultLifecycleObserver {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var searchTag by mutableStateOf("")

    var wallpaperIdx by mutableStateOf(0)
        private set
    var showExit by mutableStateOf(false)
    var showReview by mutableStateOf(false)
    private var startTime: Long = 0

    private lateinit var auth: FirebaseAuth

    fun fetchWallpapers() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        val db = Firebase.firestore
        db.collection("wallpapers")
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
            DatabaseHolder.Database.favoritesDao().getAll().collect { favoriteIds ->
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
            if (liked) DatabaseHolder.Database.favoritesDao().delete(wallpaper)
            else DatabaseHolder.Database.favoritesDao().insertAll(wallpaper)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        auth = Firebase.auth
        requestNotificationsPermission(owner as Activity)
        (owner as MainActivity).onBackPressedDispatcher
            .addCallback(owner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExit = !showExit
                }
            })
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        startTime = System.currentTimeMillis()

        auth.signInAnonymously()
            .addOnCompleteListener(owner as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FirestoreAuth", "signInAnonymously:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FirestoreAuth", "signInAnonymously:failure", task.exception)
                    Toast.makeText(
                        owner,
                        owner.getString(R.string.auth_failed),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        var timeSpent = System.currentTimeMillis() - startTime
        timeSpent += PrefHelper.getTimeSpent()
        PrefHelper.setTimeSpent(timeSpent)
    }
}
