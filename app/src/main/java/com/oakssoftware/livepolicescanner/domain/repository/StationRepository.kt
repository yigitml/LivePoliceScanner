package com.oakssoftware.livepolicescanner.domain.repository

import com.oakssoftware.livepolicescanner.data.retrofit.dto.StationsDto
import com.oakssoftware.livepolicescanner.data.room.dto.StationDto

interface StationRepository {

    suspend fun getStationsFromApi(): StationsDto
    suspend fun cacheStations(stations: List<StationDto>)
    suspend fun getStationsFromCache(): List<StationDto>
    suspend fun getStationDetail(uid: Int): StationDto

    suspend fun updateStation(station: StationDto)
    suspend fun getFavoriteStations(): List<StationDto>
}