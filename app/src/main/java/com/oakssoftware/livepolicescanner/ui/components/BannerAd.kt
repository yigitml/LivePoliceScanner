package com.oakssoftware.livepolicescanner.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.oakssoftware.livepolicescanner.util.Constants

@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = Constants.BANNER_TEST // Test Id
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId //Constants.BANNER_TEST
                loadAd(AdRequest.Builder().build())
            }
        },
        update = {
            // We do not need to update the view here, since there are no
            // parameters that can be updated for an ad.
        }
    )
}