package com.noukta.wallpaper.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun getBitmap(context: Context, resId: Int): Bitmap {
    return BitmapFactory.decodeResource(context.resources, resId)
}
