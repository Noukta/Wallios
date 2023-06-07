package com.noukta.wallpaper.admob

/**
 * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
 * dismissed or fails to show).
 */
interface AdmobAppOpenAdCallback {
    fun onAdDismissed(tag: String, message: String)

    fun onAdShowed(tag: String, message: String)
}