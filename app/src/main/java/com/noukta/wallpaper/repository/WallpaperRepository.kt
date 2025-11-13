package com.noukta.wallpaper.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.noukta.wallpaper.data.Category
import com.noukta.wallpaper.db.dao.FavoritesDao
import com.noukta.wallpaper.db.obj.Wallpaper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val favoritesDao: FavoritesDao
) {
    companion object {
        private const val TAG = "WallpaperRepository"
        private const val COLLECTION_WALLPAPERS = "wallpapers"
    }

    /**
     * Fetch wallpapers from Firestore with pagination
     */
    fun fetchWallpapers(limit: Long): Flow<Result<List<Wallpaper>>> = callbackFlow {
        firestore.collection(COLLECTION_WALLPAPERS)
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                val wallpapers = result.mapNotNull { document ->
                    try {
                        val tags = document.get("tags") as? List<String> ?: emptyList()
                        if (tags.isNotEmpty()) {
                            Wallpaper(
                                id = document.id,
                                url = document.data["url"] as? String ?: "",
                                category = Category.valueOf(
                                    tags[0].replaceFirstChar { it.titlecase(Locale.getDefault()) }
                                ),
                                tags = tags
                            )
                        } else null
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing wallpaper document: ${document.id}", e)
                        null
                    }
                }
                trySend(Result.success(wallpapers))
                Log.d(TAG, "Loaded ${wallpapers.size} wallpapers from Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching wallpapers", exception)
                trySend(Result.failure(exception))
            }

        awaitClose()
    }

    /**
     * Get all favorite wallpapers as Flow
     */
    fun getFavorites(): Flow<List<Wallpaper>> {
        return favoritesDao.getAll()
    }

    /**
     * Add wallpaper to favorites
     */
    suspend fun addToFavorites(wallpaper: Wallpaper) {
        favoritesDao.insertAll(wallpaper)
    }

    /**
     * Remove wallpaper from favorites
     */
    suspend fun removeFromFavorites(wallpaper: Wallpaper) {
        favoritesDao.delete(wallpaper)
    }

    /**
     * Check if wallpaper exists in favorites
     */
    suspend fun isFavorite(wallpaperId: String): Boolean {
        return favoritesDao.exists(wallpaperId)
    }
}
