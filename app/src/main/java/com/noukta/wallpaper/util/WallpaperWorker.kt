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
import androidx.work.workDataOf
import com.noukta.wallpaper.db.obj.Wallpaper
import com.noukta.wallpaper.ext.getBitmap
import com.noukta.wallpaper.settings.WallpaperMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val WALLPAPER_ID = "wallpaperId"
const val MODE = "mode"
const val RESULT = "result"

class WallpaperWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    companion object{
        fun setWallpaper(context: Context, wallpaper: Wallpaper, mode: Int, onFinish: () -> Unit){
            val workManager = WorkManager.getInstance(context)
            val data = Data.Builder()
                .putInt(WALLPAPER_ID, wallpaper.id)
                .putInt(MODE, mode)
                .build()
            val workRequest = OneTimeWorkRequestBuilder<WallpaperWorker>()
                .setInputData(data)
                .build()
            workManager.enqueue(workRequest)
            workManager.getWorkInfoByIdLiveData(workRequest.id)
                .observeForever {
                    if(it.state == WorkInfo.State.SUCCEEDED) {
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
        return withContext(Dispatchers.IO)
        {
            return@withContext try {
                val wallpaperId = inputData.getInt(WALLPAPER_ID, 0)
                val mode = inputData.getInt(MODE, 0)
                val bitmap = getBitmap(applicationContext, wallpaperId)
                val result = setWallpaper(applicationContext, bitmap, mode)
                val outputData = workDataOf(RESULT to result)
                Result.success(outputData)
            } catch (throwable: Throwable) {
                Result.failure()
            }
        }
    }
}