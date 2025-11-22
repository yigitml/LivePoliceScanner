package com.oakssoftware.livepolicescanner.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdPreferences @Inject constructor(@ApplicationContext private val context: Context) {
  private val prefs = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_STATION_DETAIL_INTERSTITIAL_SHOWN = "station_detail_interstitial_shown"
  }

  fun hasShownStationDetailInterstitial(): Boolean {
    return prefs.getBoolean(KEY_STATION_DETAIL_INTERSTITIAL_SHOWN, false)
  }

  fun setStationDetailInterstitialShown() {
    prefs.edit().putBoolean(KEY_STATION_DETAIL_INTERSTITIAL_SHOWN, true).apply()
  }

  // For testing purposes only - allows resetting the ad display state
  fun resetStationDetailInterstitial() {
    prefs.edit().putBoolean(KEY_STATION_DETAIL_INTERSTITIAL_SHOWN, false).apply()
  }
}
