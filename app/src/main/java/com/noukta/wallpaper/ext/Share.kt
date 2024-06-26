package com.noukta.wallpaper.ext

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import androidx.core.content.FileProvider
import com.noukta.wallpaper.BuildConfig
import com.noukta.wallpaper.R
import com.noukta.wallpaper.settings.URL.PLAY_STORE
import java.io.File
import java.io.FileOutputStream

fun shareWallpaper(context: Context, bitmap: Bitmap?) {
    val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(dir, "Wallios-Wallpaper.webp")
    val outputStream = FileOutputStream(file)
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    val uri = FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        clipData = ClipData.newRawUri(null, uri)
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, context.resources.getString(R.string.share_message, PLAY_STORE))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        type = "image/webp"
    }
    context.startActivity(Intent.createChooser(intent, "Share Wallpaper"))
}