package com.noukta.wallpaper.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ImageHelper {
    private val imageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var imageLoader: ImageLoader

    fun init(context: Context) {
        imageLoader = ImageLoader.Builder(context.applicationContext)
            .crossfade(true)
            .build()
    }

    fun urlToBitmap(
        imageURL: String?,
        context: Context,
        onSuccess: (bitmap: Bitmap) -> Unit,
        onError: (throwable: Throwable) -> Unit = {}
    ) {
        imageScope.launch {
            val request = buildRequest(context, imageURL)
                .target(
                    onSuccess = {
                        onSuccess(it.toBitmap())
                    },
                    onError = { error ->
                        onError(error ?: Exception("Unknown error loading image"))
                    }
                )
                .build()
            imageLoader.enqueue(request)
        }
    }

    private fun buildRequest(context: Context, url: String?): ImageRequest.Builder {
        return ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
    }
}