package com.oakssoftware.livepolicescanner.ui.screens.stations

import androidx.compose.runtime.Immutable
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.ui.ScreenState

@Immutable
data class StationsState(
    val screenState: ScreenState = ScreenState.Loading,
    val stations: List<Station> = emptyList(),
    val errorMessage: String = "",
    val isFavoritesOpen: Boolean = false
)