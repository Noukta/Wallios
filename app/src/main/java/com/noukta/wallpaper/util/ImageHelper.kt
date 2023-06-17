package com.noukta.wallpaper.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest

object ImageHelper {
    fun urlToBitmap(
        imageURL: String?,
        context: Context,
        onSuccess: (bitmap: Bitmap) -> Unit
    ) {
        DataScope.launch {
            val request = buildRequest(context, imageURL)
                .target {
                    onSuccess(it.toBitmap())
                }
                .build()
            ImageLoader(context).enqueue(request)
        }
    }

    private fun buildRequest(context: Context, url: String?): ImageRequest.Builder {
        return ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
    }
}