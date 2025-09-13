package com.oakssoftware.livepolicescanner.ui.screens.station_detail

data class MediaState(
    val playerState: PlayerState,
    val isConnectionEstablished: Boolean
)

enum class PlayerState {
    IDLE,
    PLAYING,
    PAUSED,
    STOPPED
}