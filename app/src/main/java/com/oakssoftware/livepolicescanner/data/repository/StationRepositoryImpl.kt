package com.oakssoftware.livepolicescanner.data.repository

import com.oakssoftware.livepolicescanner.data.retrofit.StationApi
import com.oakssoftware.livepolicescanner.data.retrofit.dto.StationsDto
import com.oakssoftware.livepolicescanner.data.room.StationDao
import com.oakssoftware.livepolicescanner.data.room.dto.StationDto
import com.oakssoftware.livepolicescanner.domain.repository.StationRepository
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val stationApi: StationApi,
    private val stationDao: StationDao
) : StationRepository {

    override suspend fun getStationsFromApi(): StationsDto =
        stationApi.getStations()

    override suspend fun cacheStations(stations: List<StationDto>) {
        stationDao.insertAll(*stations.toTypedArray())
    }

    override suspend fun getStationsFromCache(): List<StationDto> =
        stationDao.getStationsFromCache()

    override suspend fun getStationDetail(uid: Int): StationDto =
        stationDao.getStationDetails(uid)


    override suspend fun updateStation(
        station: StationDto
    ) {
        stationDao.updateStation(station)
    }

    override suspend fun getFavoriteStations(): List<StationDto> =
        stationDao.getFavoriteStations()
}