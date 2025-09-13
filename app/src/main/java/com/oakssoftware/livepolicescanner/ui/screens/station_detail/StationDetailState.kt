package com.oakssoftware.livepolicescanner.ui.screens.station_detail

import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.ui.ScreenState

data class StationDetailState(
    val screenState: ScreenState = ScreenState.Loading,
    val station: Station? = null,
    val favoriteStations: List<Station> = emptyList(),
    val errorMessage: String? = "",
)