package com.oakssoftware.livepolicescanner.ui.screens.stations.view

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.oakssoftware.livepolicescanner.R
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.ui.Screen
import com.oakssoftware.livepolicescanner.ui.ScreenState
import com.oakssoftware.livepolicescanner.ui.components.BannerAd
import com.oakssoftware.livepolicescanner.ui.screens.stations.StationsEvent
import com.oakssoftware.livepolicescanner.ui.screens.stations.StationsTab
import com.oakssoftware.livepolicescanner.ui.screens.stations.StationsViewModel
import com.oakssoftware.livepolicescanner.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
        ip: PaddingValues,
        navController: NavController,
        viewModel: StationsViewModel = hiltViewModel()
) {
        val state = viewModel.state.value
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        val activity = LocalActivity.current

        Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).padding(ip),
                topBar = {
                        StationsTopAppBar(
                                isSearching = state.isSearching,
                                searchText = state.searchText,
                                onSearchTextChanged = {
                                        viewModel.onEvent(StationsEvent.SearchQueryChanged(it))
                                },
                                onSearchClosed = { viewModel.onEvent(StationsEvent.ToggleSearch) },
                                onToggleFavorites = {
                                        viewModel.onEvent(StationsEvent.ToggleFavorites())
                                },
                                isFavoritesOpen = state.isFavoritesOpen,
                                scrollBehavior = scrollBehavior,
                                selectedTab = state.selectedTab,
                                onTabSelected = { tab ->
                                        viewModel.onEvent(StationsEvent.ChangeTab(tab))
                                }
                        )
                },
                bottomBar = {
                        BannerAd(
                                Modifier.fillMaxWidth().padding(ip).navigationBarsPadding(),
                                adUnitId = Constants.BANNER_LIST
                        )
                }
        ) { innerPadding ->
                Box(
                        Modifier.fillMaxSize(),
                        contentAlignment =
                                if (state.screenState == ScreenState.Success) Alignment.TopCenter
                                else Alignment.Center
                ) {
                        when (state.screenState) {
                                ScreenState.Error ->
                                        ErrorContent("There was an error fetching stations")
                                ScreenState.Loading -> LoadingContent()
                                ScreenState.Success ->
                                        StationsList(
                                                state.isSearching,
                                                state.searchText,
                                                innerPadding,
                                                state.stations,
                                                navController,
                                                viewModel,
                                                activity
                                        )
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsTopAppBar(
        isSearching: Boolean,
        searchText: String,
        onSearchTextChanged: (String) -> Unit,
        onSearchClosed: () -> Unit,
        onToggleFavorites: () -> Unit,
        isFavoritesOpen: Boolean,
        scrollBehavior: TopAppBarScrollBehavior,
        selectedTab: StationsTab,
        onTabSelected: (StationsTab) -> Unit
) {
        Column {
                if (isSearching) {
                        StationsSearchBar(
                                hint = "Search here",
                                searchText = searchText,
                                onSearch = onSearchTextChanged,
                                onCloseClicked = onSearchClosed
                        )
                } else {
                        CenterAlignedTopAppBar(
                                title = {
                                        Text(
                                                "Stations",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )
                                },
                                navigationIcon = {
                                        IconButton(onClick = onToggleFavorites) {
                                                Icon(
                                                        imageVector =
                                                                if (isFavoritesOpen)
                                                                        ImageVector.vectorResource(
                                                                                R.drawable
                                                                                        .baseline_favorite_24
                                                                        )
                                                                else
                                                                        ImageVector.vectorResource(
                                                                                R.drawable
                                                                                        .outline_favorite_24
                                                                        ),
                                                        contentDescription = "Favorite Stations"
                                                )
                                        }
                                },
                                actions = {
                                        IconButton(onClick = { onSearchClosed() }) {
                                                Icon(
                                                        imageVector =
                                                                ImageVector.vectorResource(
                                                                        R.drawable.outline_search_24
                                                                ),
                                                        contentDescription = "Search Stations"
                                                )
                                        }
                                },
                                scrollBehavior = scrollBehavior
                        )
                }
                TabRow(selectedTabIndex = selectedTab.ordinal) {
                        StationsTab.values().forEach { tab ->
                                Tab(
                                        selected = selectedTab == tab,
                                        onClick = { onTabSelected(tab) },
                                        text = { Text(text = tab.title) }
                                )
                        }
                }
        }
}

@Composable
fun StationsList(
        isSearching: Boolean,
        searchText: String,
        innerPadding: PaddingValues,
        stations: List<Station>,
        navController: NavController,
        viewModel: StationsViewModel,
        activity: Activity?
) {
        var shouldShowAd by remember { mutableStateOf(false) }
        var pendingStationNavigation by remember { mutableStateOf<Station?>(null) }

        // Interstitial Ad Handler
        if (shouldShowAd && pendingStationNavigation != null) {
                com.oakssoftware.livepolicescanner.ui.components.ShowInterstitialAd(
                        activity = activity,
                        adUnitId = Constants.INTERSTITIAL_MAIN,
                        shouldShow = shouldShowAd,
                        onAdDismissed = {
                                viewModel.markInterstitialShown()
                                shouldShowAd = false
                                pendingStationNavigation?.let { station ->
                                        navController.navigate(
                                                Screen.StationDetailScreen.route + "/${station.uid}"
                                        ) { launchSingleTop = true }
                                }
                                pendingStationNavigation = null
                        },
                        onAdFailed = {
                                viewModel.markInterstitialShown()
                                shouldShowAd = false
                                pendingStationNavigation?.let { station ->
                                        navController.navigate(
                                                Screen.StationDetailScreen.route + "/${station.uid}"
                                        ) { launchSingleTop = true }
                                }
                                pendingStationNavigation = null
                        }
                )
        }

        // Calculate total items including ads (1 ad every 4 stations)
        val adInterval = 4
        val numberOfAds = if (stations.size > adInterval) (stations.size - 1) / adInterval else 0
        val totalItems = stations.size + numberOfAds

        LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                items(
                        count = totalItems,
                        key = { index ->
                                // Calculate if this position should be an ad
                                val adsBeforeThisPosition = index / (adInterval + 1)
                                val isAdPosition = (index + 1) % (adInterval + 1) == 0 && index > 0

                                if (isAdPosition) {
                                        "ad_$adsBeforeThisPosition"
                                } else {
                                        val stationIndex = index - adsBeforeThisPosition
                                        if (stationIndex < stations.size) {
                                                stations[stationIndex].uid
                                        } else {
                                                "station_$stationIndex"
                                        }
                                }
                        }
                ) { index ->
                        // Calculate if this position should be an ad
                        val adsBeforeThisPosition = index / (adInterval + 1)
                        val isAdPosition = (index + 1) % (adInterval + 1) == 0 && index > 0

                        if (isAdPosition) {
                                // Show banner ad
                                BannerAd(
                                        modifier = Modifier.fillMaxWidth(),
                                        adUnitId = Constants.BANNER_LIST_ITEM
                                )
                        } else {
                                // Show station item
                                val stationIndex = index - adsBeforeThisPosition
                                if (stationIndex < stations.size) {
                                        val station = stations[stationIndex]
                                        StationListItem(
                                                station = station,
                                                onItemClick = { clickedStation ->
                                                        if (viewModel.shouldShowInterstitial()) {
                                                                pendingStationNavigation = clickedStation
                                                                shouldShowAd = true
                                                        } else {
                                                                navController.navigate(
                                                                        Screen.StationDetailScreen
                                                                                .route +
                                                                                "/${clickedStation.uid}"
                                                                ) { launchSingleTop = true }
                                                        }
                                                },
                                                onFavoriteButtonClick = {
                                                        viewModel.onEvent(
                                                                StationsEvent.UpdateStation(
                                                                        it.copy(
                                                                                isFavorite =
                                                                                        !it.isFavorite
                                                                        )
                                                                )
                                                        )
                                                }
                                        )
                                }
                        }
                }
        }
}

@Composable
fun ErrorContent(message: String) {
        Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
        )
}

@Composable
fun LoadingContent() {
        Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) { CircularProgressIndicator() }
}
