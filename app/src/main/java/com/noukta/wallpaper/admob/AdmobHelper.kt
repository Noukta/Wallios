package com.noukta.wallpaper.admob

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdmobHelper {

    private var mInterstitialAds: MutableMap<String, InterstitialAd?> = mutableMapOf()
    private var mRewardedAds: MutableMap<String, RewardedAd?> = mutableMapOf()
    private const val TAG = "AdmobHelper"

    fun init(context: Context) {
        MobileAds.initialize(context) {}
    }

    /**
     * Helper function to get Activity from Context safely
     */
    private fun Context.getActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    private fun createFullScreenContentCallback(
        adType: String,
        onAdShowed: () -> Unit,
        onAdDismissed: () -> Unit
    ): FullScreenContentCallback {
        return object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "$adType Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "$adType Ad dismissed fullscreen content.")
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.e(TAG, "$adType Ad failed to show fullscreen content.")
                onAdDismissed()
            }

            override fun onAdImpression() {
                Log.d(TAG, "$adType Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "$adType Ad showed fullscreen content.")
                onAdShowed()
            }
        }
    }

    fun loadInterstitial(
        context: Context,
        adUnit: String
    ) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnit, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Interstitial Ad $adError")
                mInterstitialAds[adUnit] = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Interstitial Ad was loaded.")
                mInterstitialAds[adUnit] = interstitialAd
            }
        })
    }

    fun showInterstitial(
        context: Context,
        adUnit: String,
        onAdShowed: () -> Unit = {},
        onAdDismissed: () -> Unit = {}
    ) {
        val activity = context.getActivity()
        if (activity == null) {
            Log.e(TAG, "Cannot show interstitial ad: context is not an Activity")
            return
        }

        val ad = mInterstitialAds[adUnit]
        if (ad != null) {
            ad.fullScreenContentCallback =
                createFullScreenContentCallback("Interstitial", onAdShowed, onAdDismissed)
            ad.show(activity)
            // Clear ad after showing as it can only be shown once
            mInterstitialAds[adUnit] = null
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    fun loadRewarded(
        context: Context,
        adUnit: String
    ) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, adUnit, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Rewarded Ad $adError")
                mRewardedAds[adUnit] = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d(TAG, "Rewarded Ad was loaded.")
                mRewardedAds[adUnit] = rewardedAd
            }
        })
    }

    fun showRewarded(
        context: Context,
        adUnit: String,
        onRewarded: () -> Unit,
        onAdShowed: () -> Unit = {},
        onAdDismissed: () -> Unit = {}
    ) {
        val activity = context.getActivity()
        if (activity == null) {
            Log.e(TAG, "Cannot show rewarded ad: context is not an Activity")
            return
        }

        val ad = mRewardedAds[adUnit]
        if (ad != null) {
            ad.fullScreenContentCallback =
                createFullScreenContentCallback("Rewarded", onAdShowed, onAdDismissed)
            ad.show(activity) {
                // Handle the reward.
                onRewarded()
            }
            // Clear ad after showing as it can only be shown once
            mRewardedAds[adUnit] = null
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }
}