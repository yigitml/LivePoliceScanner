package com.oakssoftware.livepolicescanner.ui.screens.stations.view

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.ui.Screen
import com.oakssoftware.livepolicescanner.ui.ScreenState
import com.oakssoftware.livepolicescanner.ui.components.BannerAd
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.view.ErrorContent
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.view.LoadingContent
import com.oakssoftware.livepolicescanner.ui.screens.stations.StationsEvent
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
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val activity = LocalActivity.current

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(ip),
        topBar = {
            StationsTopAppBar(
                isSearching = isSearching,
                searchText = searchText,
                onSearchTextChanged = {
                    searchText = it
                    viewModel.onEvent(StationsEvent.GetStations(isSearching, it))
                },
                onSearchClosed = { isSearching = !isSearching },
                onToggleFavorites = {
                    viewModel.onEvent(StationsEvent.ToggleFavorites(isSearching, searchText))
                },
                isFavoritesOpen = state.isFavoritesOpen,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BannerAd(Modifier.fillMaxWidth().padding(16.dp), adUnitId = Constants.BANNER_LIST_ITEM)
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (state.screenState) {
                ScreenState.Error -> ErrorContent("There was an error fetching stations")
                ScreenState.Loading -> LoadingContent()
                ScreenState.Success -> StationsList(
                    isSearching,
                    searchText,
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
    scrollBehavior: TopAppBarScrollBehavior
) {
    if (isSearching) {
        StationsSearchBar(
            hint = "Search here",
            searchText = searchText,
            onSearch = onSearchTextChanged,
            onCloseClicked = onSearchClosed
        )
    } else {
        CenterAlignedTopAppBar(
            title = { Text("Stations", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = onToggleFavorites) {
                    Icon(
                        imageVector = if (isFavoritesOpen) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite Stations"
                    )
                }
            },
            actions = {
                IconButton(onClick = { onSearchClosed() }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Stations")
                }
            },
            scrollBehavior = scrollBehavior
        )
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
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        itemsIndexed(
            items = stations,
            key = { _, station -> station.uid }
        ) { _, station ->
            StationListItem(
                station = station,
                onItemClick = {
                    val navigate = {
                        navController.navigate(Screen.StationDetailScreen.route + "/${it.uid}") { launchSingleTop = true }
                    }
                    navigate()
                },
                onFavoriteButtonClick = {
                    viewModel.onEvent(
                        StationsEvent.UpdateStation(
                            it.copy(isFavorite = !it.isFavorite),
                            isSearching,
                            searchText
                        )
                    )
                }
            )
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
    ) {
        CircularProgressIndicator()
    }
}