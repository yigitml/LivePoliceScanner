package com.oakssoftware.livepolicescanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.oakssoftware.livepolicescanner.ui.screens.about_us.AboutUsScreen
import com.oakssoftware.livepolicescanner.ui.screens.home.HomeScreen
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.view.StationDetailScreen
import com.oakssoftware.livepolicescanner.ui.screens.stations.view.StationsScreen
import com.oakssoftware.livepolicescanner.ui.theme.PoliceScannerProTheme
import com.oakssoftware.livepolicescanner.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var interstitialAd: InterstitialAd? = null
    private var adShownOnDetailScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val testDeviceIds = listOf("B4947C6876E93FB3CDC4C07FB519AD01")
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()

        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this@MainActivity) {}
        loadInterstitialAd()

        setContent {
            PoliceScannerProTheme {
                Scaffold(
                    modifier = Modifier.Companion.fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()
                    
                    // Add destination observer to show interstitial when StationDetail opens
                    ObserveNavigationAndShowAd(navController)

                    NavHost(
                        navController = navController,
                        startDestination = Screen.HomeScreen.route
                    ) {
                        composable(route = Screen.HomeScreen.route) {
                            HomeScreen(innerPadding, navController)
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

    @Composable
    private fun ObserveNavigationAndShowAd(navController: NavController) {
        val currentBackStack = navController.currentBackStackEntryAsState()
        
        LaunchedEffect(currentBackStack.value) {
            // Check if current destination is StationDetail screen
            val currentRoute = currentBackStack.value?.destination?.route
            if (currentRoute != null && 
                currentRoute.startsWith(Screen.StationDetailScreen.route) && 
                !adShownOnDetailScreen) {
                showInterstitialAd()
                adShownOnDetailScreen = true
            }
        }
    }
    
    private fun loadInterstitialAd(
        adUnitId: String = Constants.INTERSTITIAL_GENERAL
    ) {
        InterstitialAd.load(
            this,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            }
        )
    }

    private fun showInterstitialAd() {
        interstitialAd?.show(this) ?: run {
            loadInterstitialAd()
        }
    }
}