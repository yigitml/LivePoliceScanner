package com.oakssoftware.livepolicescanner.presentation.station_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.domain.use_case.get_station_detail.GetStationDetailUseCase
import com.oakssoftware.livepolicescanner.domain.use_case.get_stations.GetStationsUseCase
import com.oakssoftware.livepolicescanner.domain.use_case.update_stations.UpdateStationsUseCase
import com.oakssoftware.livepolicescanner.presentation.ScreenState
import com.oakssoftware.livepolicescanner.util.Constants
import com.oakssoftware.livepolicescanner.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StationDetailViewModel @Inject constructor(
    private val getStationDetailUseCase: GetStationDetailUseCase,
    private val getStationsUseCase: GetStationsUseCase,
    private val updateStationsUseCase: UpdateStationsUseCase,
    stateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(StationDetailState())
    val state: State<StationDetailState> = _state

    init {
        val stationId = stateHandle.get<String>(Constants.STATION_ID)
        when {
            stationId != null -> {
                getStationDetail(stationId.toInt())
                getFavoriteStations()
            }

            else -> {
                _state.value = StationDetailState(
                    screenState = ScreenState.Error,
                    station = null,
                    favoriteStations = _state.value.favoriteStations,
                    errorMessage = "Error getting station detail",
                    isPlaying = false
                )
            }
        }
    }

    private fun handleFavoriteStationsResource(resource: Resource<List<Station>>) {
        _state.value = when (resource) {
            is Resource.Success -> StationDetailState(
                screenState = _state.value.screenState,
                station = _state.value.station,
                favoriteStations = resource.data ?: emptyList(),
                errorMessage = resource.message,
                isPlaying = _state.value.isPlaying
            )

            is Resource.Loading -> StationDetailState(
                screenState = _state.value.screenState,
                station = _state.value.station,
                favoriteStations = emptyList(),
                errorMessage = resource.message,
                isPlaying = _state.value.isPlaying
            )

            is Resource.Error -> StationDetailState(
                screenState = _state.value.screenState,
                station = _state.value.station,
                favoriteStations = emptyList(),
                errorMessage = resource.message,
                isPlaying = _state.value.isPlaying
            )
        }
    }

    private fun handleResource(resource: Resource<Station>) {
        _state.value = when (resource) {
            is Resource.Success -> StationDetailState(
                screenState = ScreenState.Success,
                station = resource.data,
                favoriteStations = _state.value.favoriteStations,
                errorMessage = resource.message,
                isPlaying = _state.value.isPlaying
            )

            is Resource.Loading -> StationDetailState(
                screenState = ScreenState.Loading,
                station = null,
                favoriteStations = _state.value.favoriteStations,
                errorMessage = resource.message,
                isPlaying = _state.value.isPlaying
            )

            is Resource.Error -> StationDetailState(
                screenState = ScreenState.Error,
                station = null,
                favoriteStations = _state.value.favoriteStations,
                errorMessage = resource.message,
                isPlaying = _state.value.isPlaying
            )
        }
    }

    private fun getStationDetail(stationId: Int) {
        getStationDetailUseCase.executeGetStationDetail(stationId)
            .onEach { handleResource(it) }
            .launchIn(viewModelScope)
    }

    private fun getFavoriteStations() {
        getStationsUseCase.executeGetStations(isFavToggleOpen = true)
            .onEach { handleFavoriteStationsResource(it) }
            .launchIn(viewModelScope)
    }

    private fun updateStation(station: Station) {
        updateStationsUseCase.executeUpdateStation(station)
            .onEach { handleResource(it) }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: StationDetailEvent) {
        when (event) {
            is StationDetailEvent.UpdateStation -> {
                updateStation(event.station)
            }

            is StationDetailEvent.UpdatePlayState -> {
                _state.value = StationDetailState(
                    screenState = _state.value.screenState,
                    station = _state.value.station,
                    favoriteStations = _state.value.favoriteStations,
                    errorMessage = _state.value.errorMessage,
                    isPlaying = event.isPlaying
                )
            }
        }
    }
}