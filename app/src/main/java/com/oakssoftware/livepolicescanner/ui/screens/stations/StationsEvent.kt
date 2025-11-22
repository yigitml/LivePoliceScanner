package com.oakssoftware.livepolicescanner.ui.screens.stations

import com.oakssoftware.livepolicescanner.domain.model.Station

sealed class StationsEvent {
        data class SearchQueryChanged(val query: String) : StationsEvent()
        data class UpdateStation(val station: Station) : StationsEvent()
        data class ToggleFavorites(val isFavoritesOpen: Boolean? = null) : StationsEvent()
        data class ChangeTab(val tab: StationsTab) : StationsEvent()
        object ToggleSearch : StationsEvent()
}
