package com.oakssoftware.livepolicescanner.domain.use_case.get_stations

import com.oakssoftware.livepolicescanner.data.repository.StationRepositoryImpl
import com.oakssoftware.livepolicescanner.data.retrofit.dto.toStationDtoList
import com.oakssoftware.livepolicescanner.data.retrofit.dto.toStationList
import com.oakssoftware.livepolicescanner.data.room.dto.StationDto
import com.oakssoftware.livepolicescanner.data.room.dto.toStation
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetStationsUseCase @Inject constructor(
    private val repository: StationRepositoryImpl
) {
    private suspend fun fetchAndCacheStations() {
        val cacheResponse = repository.getStationsFromCache()
        if (cacheResponse.isEmpty()) {
            val apiResponse = repository.getStationsFromApi()
            repository.cacheStations(apiResponse.toStationDtoList())
        }
    }

    fun executeGetStations(
        isSearching: Boolean = false,
        search: String = "",
        isFavToggleOpen: Boolean = false
    ): Flow<Resource<List<Station>>> = flow {
        try {
            emit(Resource.Loading())

            fetchAndCacheStations()

            val stations = if (isFavToggleOpen) {
                repository.getFavoriteStations()
            } else {
                repository.getStationsFromCache()
            }

            val result = if (isSearching) {
                stations.filter { it.search.contains(search, ignoreCase = true) }
            } else {
                stations
            }

            emit(Resource.Success(result.map { it.toStation() }))
        } catch (e: HttpException) {
            emit(Resource.Error(error = e, message = "Network error"))
        } catch (e: Exception) {
            emit(Resource.Error(error = e, message = "Error getting stations"))
        }
    }
}