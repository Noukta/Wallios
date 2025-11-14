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

            // Use observeOnce pattern to avoid memory leak
            val observer = object : androidx.lifecycle.Observer<WorkInfo> {
                override fun onChanged(value: WorkInfo) {
                    if (value.state == WorkInfo.State.SUCCEEDED ||
                        value.state == WorkInfo.State.FAILED ||
                        value.state == WorkInfo.State.CANCELLED) {
                        workManager.getWorkInfoByIdLiveData(workRequest.id).removeObserver(this)
                        if (value.state == WorkInfo.State.SUCCEEDED) {
                            onFinish()
                        }
                    }
                }
            }
            workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever(observer)
        }
    }

    private fun setWallpaperUp(context: Context, imageBitmap: Bitmap, mode: Int): Boolean {

        val wallpaperManager = WallpaperManager.getInstance(context)

        if (!wallpaperManager.isWallpaperSupported)
            return false
        return wallpaperManager.setBitmap(imageBitmap, null, true, mode) > 0
    }

    private fun setWallpaper(context: Context, bitmap: Bitmap, mode: Int): Boolean {
        var result = false
        val resizedBitmap = getResizedBitmap(bitmap, context.resources.displayMetrics)
        if (mode in listOf(WallpaperMode.BOTH, WallpaperMode.HOME)) {
            result = setWallpaperUp(context, resizedBitmap, WallpaperManager.FLAG_SYSTEM)
        }
        if (mode in listOf(WallpaperMode.BOTH, WallpaperMode.LOCK)) {
            result = setWallpaperUp(context, resizedBitmap, WallpaperManager.FLAG_LOCK)
        }
        return result
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
            false
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

            var success = false
            ImageHelper.urlToBitmap(
                wallpaperUrl,
                applicationContext,
                onSuccess = { bitmap ->
                    success = setWallpaper(applicationContext, bitmap, mode)
                    if (!success) {
                        Log.e(TAG, "Failed to set wallpaper")
                    }
                },
                onError = { error ->
                    Log.e(TAG, "Failed to load image: ${error.message}", error)
                }
            )

            if (success) Result.success() else Result.failure()
        } catch (throwable: Throwable) {
            Log.e(TAG, "Error in doWork: ${throwable.message}", throwable)
            Result.failure()
        }
    }
}