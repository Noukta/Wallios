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
                        // Safely parse tags list
                        @Suppress("UNCHECKED_CAST")
                        val tags = (document.get("tags") as? List<*>)
                            ?.filterIsInstance<String>()
                            ?: emptyList()

                        if (tags.isEmpty()) {
                            Log.w(TAG, "Document ${document.id} has no tags, skipping")
                            return@mapNotNull null
                        }

                        // Safely parse URL
                        val url = document.getString("url")
                        if (url.isNullOrBlank()) {
                            Log.w(TAG, "Document ${document.id} has no valid URL, skipping")
                            return@mapNotNull null
                        }

                        // Safely parse category from first tag
                        val categoryName = tags[0].replaceFirstChar { it.titlecase(Locale.getDefault()) }
                        val category = try {
                            Category.valueOf(categoryName)
                        } catch (e: IllegalArgumentException) {
                            Log.w(TAG, "Document ${document.id} has invalid category: $categoryName, defaulting to Iphone")
                            Category.Iphone
                        }

                        Wallpaper(
                            id = document.id,
                            url = url,
                            category = category,
                            tags = tags
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing wallpaper document: ${document.id}", e)
                        null
                    }
                }

                val sendResult = trySend(Result.success(wallpapers))
                if (sendResult.isFailure) {
                    Log.e(TAG, "Failed to send wallpapers to flow")
                }
                Log.d(TAG, "Loaded ${wallpapers.size} wallpapers from Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching wallpapers", exception)
                val sendResult = trySend(Result.failure(exception))
                if (sendResult.isFailure) {
                    Log.e(TAG, "Failed to send error to flow")
                }
            }

        awaitClose {
            // Cleanup if needed
            Log.d(TAG, "Closing wallpapers flow")
        }
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
