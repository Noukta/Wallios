package com.noukta.wallpaper.ext

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.noukta.wallpaper.MainActivity
import com.noukta.wallpaper.R
import com.noukta.wallpaper.settings.Notification.POST_NOTIFICATIONS_PERMISSION_REQUEST_INTERVAL
import com.noukta.wallpaper.util.PrefHelper

// Post Notifications Permission Request
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun requestNotificationsPermission(context: Context) {
    val permission = Manifest.permission.POST_NOTIFICATIONS
    val isPermissionGranted = (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED)

    if(isPermissionGranted)
        return

    val lastPostNotificationsRequestTime = PrefHelper.getLastPostNotificationsRequestTime()
    val currentTime = System.currentTimeMillis()
    val isPermissionRequestAvailable = when{
        lastPostNotificationsRequestTime == 0L -> true
        (currentTime - lastPostNotificationsRequestTime
                >= POST_NOTIFICATIONS_PERMISSION_REQUEST_INTERVAL) ->
            true
        else -> false

    }
    if(!isPermissionRequestAvailable)
        return

    val requestPermissionLauncher =
        (context as MainActivity).registerForActivityResult(ActivityResultContracts.RequestPermission()){}

    when {
        shouldShowRequestPermissionRationale(context, permission) -> {
            val dialog = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.notification_permission_title))
                .setMessage(context.getString(R.string.notification_permission_message))
                .setPositiveButton(context.getString(R.string.permission_ok)) { _, _ ->
                    requestPermissionLauncher.launch(permission)
                }
                .create()
            dialog.show()
        }

        else -> {
            requestPermissionLauncher.launch(permission)
        }
    }
    PrefHelper.resetLastPostNotificationsRequestTime()
}