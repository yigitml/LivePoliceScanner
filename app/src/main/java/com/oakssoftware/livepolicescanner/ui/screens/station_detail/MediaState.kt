package com.oakssoftware.livepolicescanner.ui.screens.station_detail

data class MediaState(
        val playerState: PlayerState,
        val isConnectionEstablished: Boolean,
        val connectionQuality: ConnectionQuality = ConnectionQuality.UNKNOWN,
        val listeningDurationSeconds: Long = 0
)

enum class PlayerState {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    STOPPED
}

enum class ConnectionQuality {
    UNKNOWN,
    EXCELLENT,
    GOOD,
    FAIR,
    POOR
}
