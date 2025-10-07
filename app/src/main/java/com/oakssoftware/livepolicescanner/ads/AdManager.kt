package com.oakssoftware.livepolicescanner.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var lastShownTimestampMs: Long = 0
    private var sessionShowCount: Int = 0

    private const val COOLDOWN_MS = 120_000L
    private const val SESSION_CAP = 3

    private var isLoading: Boolean = false
    private var currentAdUnitId: String? = null

    fun preload(context: Context, adUnitId: String) {
        currentAdUnitId = adUnitId
        if (interstitialAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }
            }
        )
    }

    private fun eligibleToShow(now: Long = System.currentTimeMillis()): Boolean {
        if (sessionShowCount >= SESSION_CAP) return false
        if (now - lastShownTimestampMs < COOLDOWN_MS) return false
        return interstitialAd != null
    }

    fun showIfEligible(activity: Activity, onContinue: () -> Unit) {
        if (eligibleToShow()) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    lastShownTimestampMs = System.currentTimeMillis()
                    sessionShowCount += 1
                    interstitialAd = null
                    currentAdUnitId?.let { preload(activity, it) }
                    onContinue()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    currentAdUnitId?.let { preload(activity, it) }
                    onContinue()
                }
            }
            interstitialAd?.show(activity)
        } else {
            if (interstitialAd == null) {
                currentAdUnitId?.let { preload(activity, it) }
            }
            onContinue()
        }
    }
}


