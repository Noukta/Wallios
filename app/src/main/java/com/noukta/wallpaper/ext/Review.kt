package com.noukta.wallpaper.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.noukta.wallpaper.settings.URL

fun review(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL.PLAY_STORE))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}