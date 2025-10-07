package com.oakssoftware.livepolicescanner.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.oakssoftware.livepolicescanner.ui.screens.about_us.AboutUsScreen
import com.oakssoftware.livepolicescanner.ui.screens.home.HomeScreen
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.view.StationDetailScreen
import com.oakssoftware.livepolicescanner.ui.screens.stations.view.StationsScreen
import com.oakssoftware.livepolicescanner.ui.theme.PoliceScannerProTheme
import com.oakssoftware.livepolicescanner.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import com.oakssoftware.livepolicescanner.ads.AdManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val testDeviceIds = listOf("B4947C6876E93FB3CDC4C07FB519AD01")
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()

        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this@MainActivity) {}
        AdManager.preload(this, Constants.INTERSTITIAL_TEST)

        setContent {
            PoliceScannerProTheme {
                Scaffold(
                    modifier = Modifier.Companion.fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.HomeScreen.route
                    ) {
                        composable(route = Screen.HomeScreen.route) {
                            HomeScreen(innerPadding, navController, {
                                val manager = ReviewManagerFactory.create(this@MainActivity)

                                val request = manager.requestReviewFlow()
                                request.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val reviewInfo = task.result
                                        val flow =
                                            manager.launchReviewFlow(this@MainActivity, reviewInfo)
                                        flow.addOnCompleteListener {
                                            println("flow.onComplete")
                                        }
                                    } else {
                                        @ReviewErrorCode val reviewErrorCode = (task.getException() as ReviewException).errorCode
                                        println(reviewErrorCode)
                                    }
                                }
                            }) {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "https://play.google.com/store/apps/details?id=com.oakssoftware.livepolicescanner"
                                    )
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                startActivity(shareIntent)
                            }
                        }

                        composable(route = Screen.StationsScreen.route) {
                            StationsScreen(innerPadding, navController)
                        }

                        composable(route = Screen.StationDetailScreen.route + "/{${Constants.STATION_ID}}") {
                            StationDetailScreen(innerPadding)
                        }

                        composable(route = Screen.AboutUsScreen.route) {
                            AboutUsScreen(innerPadding)
                        }
                    }
                }
            }
        }
    }
}