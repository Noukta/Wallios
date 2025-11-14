package com.noukta.wallpaper.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.noukta.wallpaper.settings.WallpaperMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val WALLPAPER_URL = "wallpaper_url"
const val MODE = "mode"

class WallpaperWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    companion object {
        private const val TAG = "WallpaperWorker"

        fun setWallpaper(context: Context, url: String, mode: Int, onFinish: () -> Unit) {
            val workManager = WorkManager.getInstance(context)
            val data = Data.Builder()
                .putString(WALLPAPER_URL, url)
                .putInt(MODE, mode)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<WallpaperWorker>()
                .setInputData(data)
                .build()
            workManager.enqueue(workRequest)

            // Use coroutine with Flow to avoid memory leak from observeForever
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val workInfo = workManager.getWorkInfoByIdFlow(workRequest.id)
                        .first { info ->
                            info.state == WorkInfo.State.SUCCEEDED ||
                            info.state == WorkInfo.State.FAILED ||
                            info.state == WorkInfo.State.CANCELLED
                        }

                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        onFinish()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error observing work status", e)
                }
            }
        }
    }

    private fun setWallpaperUp(context: Context, imageBitmap: Bitmap, mode: Int): Boolean {

        val wallpaperManager = WallpaperManager.getInstance(context)

        if (!wallpaperManager.isWallpaperSupported)
            return false
        return wallpaperManager.setBitmap(imageBitmap, null, true, mode) > 0
    }

    private fun setWallpaper(context: Context, bitmap: Bitmap, mode: Int): Boolean {
        val resizedBitmap = getResizedBitmap(bitmap, context.resources.displayMetrics)
        var homeResult = true
        var lockResult = true

        if (mode in listOf(WallpaperMode.BOTH, WallpaperMode.HOME)) {
            homeResult = setWallpaperUp(context, resizedBitmap, WallpaperManager.FLAG_SYSTEM)
        }
        if (mode in listOf(WallpaperMode.BOTH, WallpaperMode.LOCK)) {
            lockResult = setWallpaperUp(context, resizedBitmap, WallpaperManager.FLAG_LOCK)
        }
        // Both operations must succeed for BOTH mode
        return homeResult && lockResult
    }

    private fun getResizedBitmap(bitmap: Bitmap, displayMetrics: DisplayMetrics): Bitmap {
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        val bitmapRatio = bitmapHeight / bitmapWidth
        val screenRatio = screenHeight / screenWidth

        return if (screenRatio > bitmapRatio) {
            getResizedBitmap(bitmap, screenWidth, (screenWidth * bitmapRatio).toInt())
        } else {
            getResizedBitmap(bitmap, (screenHeight / bitmapRatio).toInt(), screenHeight)
        }
    }

    private fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            newHeight,
            true  // Enable filtering for better image quality
        )
    }

    override suspend fun doWork(): Result {
        return try {
            val wallpaperUrl = inputData.getString(WALLPAPER_URL).orEmpty()
            val mode = inputData.getInt(MODE, 0)

            if (wallpaperUrl.isEmpty()) {
                Log.e(TAG, "Wallpaper URL is empty")
                return Result.failure()
            }

            // Use suspendCoroutine to properly wait for the async callback
            val success = suspendCoroutine { continuation ->
                ImageHelper.urlToBitmap(
                    wallpaperUrl,
                    applicationContext,
                    onSuccess = { bitmap ->
                        val result = setWallpaper(applicationContext, bitmap, mode)
                        if (!result) {
                            Log.e(TAG, "Failed to set wallpaper")
                        }
                        continuation.resume(result)
                    },
                    onError = { error ->
                        Log.e(TAG, "Failed to load image: ${error.message}", error)
                        continuation.resume(false)
                    }
                )
            }

            if (success) Result.success() else Result.failure()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error in doWork: ${throwable.message}", throwable)
            Result.failure()
        }
    }
}