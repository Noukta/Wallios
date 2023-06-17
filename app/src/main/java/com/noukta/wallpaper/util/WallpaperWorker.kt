package com.noukta.wallpaper.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
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
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .observeForever {
                    if (it.state == WorkInfo.State.SUCCEEDED) {
                        onFinish()
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

                ImageHelper.urlToBitmap(wallpaperUrl, applicationContext){
                    setWallpaper(applicationContext, it, mode)
                }
                Result.success()
            } catch (throwable: Throwable) {
                Result.failure()
            }
    }
}