package com.oakssoftware.livepolicescanner.domain.use_case.update_stations

import com.oakssoftware.livepolicescanner.data.repository.StationRepositoryImpl
import com.oakssoftware.livepolicescanner.data.room.dto.toStation
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.domain.model.toStationDto
import com.oakssoftware.livepolicescanner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class UpdateStationsUseCase @Inject constructor(
    private val repository: StationRepositoryImpl
) {

    fun executeUpdateStation(station: Station): Flow<Resource<Station>> = flow {
        try {
            emit(Resource.Loading())
            repository.updateStation(station.toStationDto())
            val response = repository.getStationDetail(station.uid).toStation()
            emit(Resource.Success(response))
        } catch (e: Exception) {
            emit(Resource.Error(error = e, message = "Error updating station"))
        }
    }
}