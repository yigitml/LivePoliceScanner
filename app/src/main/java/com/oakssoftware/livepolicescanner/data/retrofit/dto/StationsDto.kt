package com.oakssoftware.livepolicescanner.data.retrofit.dto

import com.oakssoftware.livepolicescanner.data.room.dto.StationDto
import com.oakssoftware.livepolicescanner.domain.model.Station

class StationsDto : ArrayList<StationsDtoItem>()

fun StationsDto.toStationList(): List<Station> {
    return this.map {
        Station(
            uid = it.uid,
            name = it.name,
            location = it.location,
            search = it.search,
            isFavorite = false
        )
    }
}

fun StationsDto.toStationDtoList(): List<StationDto> {
    return this.map {
        StationDto(
            uid = it.uid,
            name = it.name,
            location = it.location,
            search = it.search,
            isFavorite = false
        )
    }
}