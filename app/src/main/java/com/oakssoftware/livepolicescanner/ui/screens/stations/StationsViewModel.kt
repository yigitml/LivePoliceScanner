package com.oakssoftware.livepolicescanner.ui.screens.stations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.domain.use_case.get_stations.GetStationsUseCase
import com.oakssoftware.livepolicescanner.domain.use_case.update_stations.UpdateStationsUseCase
import com.oakssoftware.livepolicescanner.ui.ScreenState
import com.oakssoftware.livepolicescanner.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StationsViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase,
    private val updateStationsUseCase: UpdateStationsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(StationsState())
    val state: State<StationsState> = _state

    private var searchJob: Job? = null

    init {
        getStations(isSearching = false, searchQuery = "")
    }

    private fun handleStationsResource(resource: Resource<List<Station>>) {
        _state.value = state.value.copy(
            screenState = when (resource) {
                is Resource.Success -> ScreenState.Success
                is Resource.Loading -> ScreenState.Loading
                is Resource.Error -> ScreenState.Error
            },
            stations = resource.data ?: emptyList(),
            errorMessage = resource.message ?: "Error!"
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun getStations(isSearching: Boolean, searchQuery: String) {
        searchJob?.cancel()
        searchJob = flow { emit(searchQuery) }
            .flatMapLatest { query ->
                if (isSearching) delay(300)
                getStationsUseCase.executeGetStations(
                    isSearching,
                    query,
                    state.value.isFavoritesOpen
                )
            }
            .onEach { handleStationsResource(it) }
            .launchIn(viewModelScope)
    }

    private fun updateStation(station: Station) {
        updateStationsUseCase.executeUpdateStation(station)
            .onEach { resource ->
                if (resource is Resource.Success) {
                    val updatedStations = state.value.stations.map {
                        if (it.uid == station.uid && !state.value.isFavoritesOpen) station else it
                    }
                    _state.value = state.value.copy(stations = updatedStations)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: StationsEvent) {
        when (event) {
            is StationsEvent.GetStations -> getStations(event.isSearching, event.search)
            is StationsEvent.UpdateStation -> updateStation(event.station)
            is StationsEvent.ToggleFavorites -> {
                _state.value = state.value.copy(isFavoritesOpen = !state.value.isFavoritesOpen)
                getStations(event.isSearching, event.search)
            }
        }
    }
}