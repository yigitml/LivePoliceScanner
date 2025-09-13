package com.oakssoftware.livepolicescanner.ui.screens.station_detail

import com.oakssoftware.livepolicescanner.domain.model.Station

sealed class StationDetailEvent {
    data class UpdateStation(val station: Station) : StationDetailEvent()
    data class UpdatePlayer(val player: MediaPlayerActions, val url: String? = null) : StationDetailEvent()

    enum class MediaPlayerActions {
        PLAY,
        PAUSE,
        STOP
    }
}