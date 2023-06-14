package com.noukta.wallpaper.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun getBitmap(context: Context, resUrl: String): Bitmap {
    // TODO: bitmap from url 
    return BitmapFactory.decodeResource(context.resources, 0)
}
