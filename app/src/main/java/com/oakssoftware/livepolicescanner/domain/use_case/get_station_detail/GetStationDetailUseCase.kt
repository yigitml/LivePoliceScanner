package com.oakssoftware.livepolicescanner.domain.use_case.get_station_detail

import com.oakssoftware.livepolicescanner.data.repository.StationRepositoryImpl
import com.oakssoftware.livepolicescanner.data.room.dto.toStation
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStationDetailUseCase @Inject constructor(
    private val repository: StationRepositoryImpl
) {

    fun executeGetStationDetail(uid: Int): Flow<Resource<Station>> = flow {
        try {
            emit(Resource.Loading())
            val stationDetailDto = repository.getStationDetail(uid)
            val station = stationDetailDto.toStation()
            emit(Resource.Success(station))
        } catch (e: java.io.IOException) {
            emit(Resource.Error(message = "No internet connection!", error = e))
        } catch (e: Exception) {
            emit(Resource.Error(message = "Error!", error = e))
        }
    }
}