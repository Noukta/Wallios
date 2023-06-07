package com.noukta.wallpaper.admob

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdBanner(adUnit: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = {
                AdView(it).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = adUnit
                    loadAd(AdRequest.Builder().build())
                    adListener = object : AdListener() {
                        override fun onAdClicked() {
                            Log.d("AdBanner", "Ad was clicked.")
                        }

                        override fun onAdClosed() {
                            Log.d("AdBanner", "Ad closed.")
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.d("AdBanner", "Ad failed to load.")
                        }

                        override fun onAdImpression() {
                            Log.d("AdBanner", "Ad impression registered.")
                        }

                        override fun onAdLoaded() {
                            Log.d("AdBanner", "Ad was loaded.")
                        }

                        override fun onAdOpened() {
                            Log.d("AdBanner", "Ad opened.")
                        }
                    }
                }
            }
        )
    }
}