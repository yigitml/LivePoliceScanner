package com.oakssoftware.livepolicescanner.ui

sealed class Screen (val route: String) {
    data object HomeScreen : Screen("home_screen")
    data object StationsScreen : Screen("stations_screen")
    data object StationDetailScreen : Screen("station_detail_screen")
    data object AboutUsScreen : Screen("about_us_screen")
}

sealed class ScreenState{
    data object Success : ScreenState()
    data object Loading : ScreenState()
    data object Error : ScreenState()
}