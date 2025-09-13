package com.oakssoftware.livepolicescanner.domain.model

import androidx.compose.runtime.Immutable
import com.oakssoftware.livepolicescanner.data.room.dto.StationDto

@Immutable
data class Station (
    val uid: Int,
    val name: String,
    val location: String,
    val search: String,
    val isFavorite: Boolean
)

fun Station.toStationDto(): StationDto {
    return StationDto(this.uid, this.name, this.location, this.search, this.isFavorite)
}