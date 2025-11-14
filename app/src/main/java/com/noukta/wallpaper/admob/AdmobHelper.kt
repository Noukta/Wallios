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

    fun init(
        context: Context,
        interstitialUnits: List<String> = listOf(),
        rewardedUnits: List<String> = listOf()
    ) {
        MobileAds.initialize(context) {}
        interstitialUnits.forEach {
            mInterstitialAds[it] = null
        }
        rewardedUnits.forEach {
            mRewardedAds[it] = null
        }
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
        adUnit: String,
        onAdShowed: () -> Unit = {},
        onAdDismissed: () -> Unit = {}
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
                mInterstitialAds[adUnit]?.fullScreenContentCallback =
                    createFullScreenContentCallback("Interstitial", onAdShowed, onAdDismissed)
            }
        })
    }

    fun showInterstitial(context: Context, adUnit: String) {
        val activity = context.getActivity()
        if (activity == null) {
            Log.e(TAG, "Cannot show interstitial ad: context is not an Activity")
            return
        }

        if (mInterstitialAds[adUnit] != null) {
            mInterstitialAds[adUnit]?.show(activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    fun loadRewarded(
        context: Context,
        adUnit: String,
        onAdShowed: () -> Unit = {},
        onAdDismissed: () -> Unit = {}
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
                mRewardedAds[adUnit]?.fullScreenContentCallback =
                    createFullScreenContentCallback("Rewarded", onAdShowed, onAdDismissed)
            }
        })
    }

    fun showRewarded(context: Context, adUnit: String, onRewarded: () -> Unit) {
        val activity = context.getActivity()
        if (activity == null) {
            Log.e(TAG, "Cannot show rewarded ad: context is not an Activity")
            return
        }

        if (mRewardedAds[adUnit] != null) {
            mRewardedAds[adUnit]?.show(activity) {
                // Handle the reward.
                onRewarded()
            }
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }
}