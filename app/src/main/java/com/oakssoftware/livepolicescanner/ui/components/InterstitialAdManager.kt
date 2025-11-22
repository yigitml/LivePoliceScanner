package com.oakssoftware.livepolicescanner.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun ShowInterstitialAd(
        activity: Activity?,
        adUnitId: String,
        shouldShow: Boolean,
        onAdDismissed: () -> Unit,
        onAdFailed: () -> Unit
) {
  var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
  var isLoading by remember { mutableStateOf(false) }
  var hasShown by remember { mutableStateOf(false) }

  // Load the ad when shouldShow becomes true
  LaunchedEffect(shouldShow) {
    if (shouldShow && !isLoading && interstitialAd == null && !hasShown && activity != null) {
      isLoading = true

      val adRequest = AdRequest.Builder().build()

      InterstitialAd.load(
              activity,
              adUnitId,
              adRequest,
              object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                  interstitialAd = ad
                  isLoading = false

                  // Set up full screen content callback
                  ad.fullScreenContentCallback =
                          object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                              interstitialAd = null
                              hasShown = true
                              onAdDismissed()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                              interstitialAd = null
                              hasShown = true
                              onAdFailed()
                            }

                            override fun onAdShowedFullScreenContent() {
                              // Ad showed successfully
                            }
                          }

                  // Show the ad immediately after loading
                  ad.show(activity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                  interstitialAd = null
                  isLoading = false
                  hasShown = true
                  onAdFailed()
                }
              }
      )
    }
  }

  // Clean up if the composable is disposed
  DisposableEffect(Unit) {
    onDispose {
      interstitialAd?.fullScreenContentCallback = null
      interstitialAd = null
    }
  }
}
