package com.noukta.wallpaper.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date


/** class that loads and shows app open ads. */
class AppOpenAdManager {

    companion object {
        const val LOG_TAG = "AppOpenAdManager"
    }

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    fun loadAd(context: Context, appOpenAdUnitId: String) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            appOpenAdUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(LOG_TAG, "onAdLoaded.")
                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)
                }
            }
        )
    }

    /** Check if ad was loaded more than 4 hours ago. */
    private fun wasLoadTimeLessThan4HoursAgo(): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThan4HoursAgo()
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    fun showAdIfAvailable(activity: Activity, appOpenAdUnitId: String, dismissAd: () -> Unit = {}) {
        showAdIfAvailable(
            activity,
            appOpenAdUnitId,
            object : AdmobAppOpenAdCallback {
                override fun onAdDismissed(tag: String, message: String) {
                    dismissAd()
                }

                override fun onAdShowed(tag: String, message: String) {

                }
            }
        )
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param admobAppOpenAdCallback the listener to be notified when an app open ad is complete
     */
    private fun showAdIfAvailable(
        activity: Activity,
        appOpenAdUnitId: String,
        admobAppOpenAdCallback: AdmobAppOpenAdCallback
    ) {
        Log.d(LOG_TAG, "The app open will show If Available.")

        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            Log.d(LOG_TAG, "The app open ad is already showing.")
            return
        }
        Log.d(LOG_TAG, "The app open ad is not showing.")
        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.")
            loadAd(activity, appOpenAdUnitId)
            return
        }

        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            override fun onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false
                Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                admobAppOpenAdCallback.onAdDismissed(LOG_TAG, "onAdDismissedFullScreenContent")
                loadAd(activity, appOpenAdUnitId)
            }

            /** Called when fullscreen content failed to show. */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)
                admobAppOpenAdCallback.onAdDismissed(
                    LOG_TAG,
                    "onAdFailedToShowFullScreenContent: " + adError.message
                )
                loadAd(activity, appOpenAdUnitId)
            }

            /** Called when fullscreen content is shown. */
            override fun onAdShowedFullScreenContent() {
                admobAppOpenAdCallback.onAdShowed(LOG_TAG, "onAdShowedFullScreenContent")
                Log.d(LOG_TAG, "onAdShowedFullScreenContent.")
            }
        }
        isShowingAd = true
        appOpenAd!!.show(activity)
    }
}