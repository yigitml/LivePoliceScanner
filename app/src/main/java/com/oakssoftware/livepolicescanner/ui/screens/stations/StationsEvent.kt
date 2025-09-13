package com.oakssoftware.livepolicescanner.ui.screens.stations

import com.oakssoftware.livepolicescanner.domain.model.Station

sealed class StationsEvent {
    data class GetStations(val isSearching: Boolean, val search: String) : StationsEvent()
    data class UpdateStation(val station: Station, val isSearching: Boolean, val search: String) : StationsEvent()
    data class ToggleFavorites(val isSearching: Boolean, val search: String) : StationsEvent()
}