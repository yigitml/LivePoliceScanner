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
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class StationsViewModel
@Inject
constructor(
        private val getStationsUseCase: GetStationsUseCase,
        private val updateStationsUseCase: UpdateStationsUseCase
) : ViewModel() {

    companion object {
        private val POPULAR_STATION_IDS =
                listOf(
                        12876,
                        32602,
                        11446,
                        14395,
                        14500,
                        30088,
                        5725,
                        13549,
                        41557,
                        19080,
                        6007,
                        4142,
                        25304,
                        26933,
                        38250,
                        31174,
                        37907,
                        41475,
                        13928,
                        21738,
                        37361,
                        5688,
                        23096,
                        10277,
                        17102
                )
    }

    private val _state = mutableStateOf(StationsState())
    val state: State<StationsState> = _state

    private var searchJob: Job? = null

    init {
        getStations()
    }

    private fun handleStationsResource(resource: Resource<List<Station>>) {
        _state.value =
                state.value.copy(
                        screenState =
                                when (resource) {
                                    is Resource.Success -> ScreenState.Success
                                    is Resource.Loading -> ScreenState.Loading
                                    is Resource.Error -> ScreenState.Error
                                },
                        stations = resource.data ?: emptyList(),
                        errorMessage = resource.message ?: "Error!"
                )
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun getStations() {
        val isSearching = state.value.isSearching
        val searchQuery = state.value.searchText
        searchJob?.cancel()
        searchJob =
                flow { emit(searchQuery) }
                        .flatMapLatest { query ->
                            if (isSearching) delay(300)
                            getStationsUseCase.executeGetStations(
                                    isSearching,
                                    query,
                                    state.value.isFavoritesOpen
                            )
                        }
                        .onEach { resource ->
                            val filteredResource =
                                    if (resource is Resource.Success) {
                                        val filteredStations =
                                                if (state.value.selectedTab == StationsTab.Popular
                                                ) {
                                                    resource.data?.filter {
                                                        it.uid in POPULAR_STATION_IDS
                                                    }
                                                            ?: emptyList()
                                                } else {
                                                    resource.data ?: emptyList()
                                                }
                                        Resource.Success(filteredStations)
                                    } else {
                                        resource
                                    }
                            handleStationsResource(filteredResource)
                        }
                        .launchIn(viewModelScope)
    }

    private fun updateStation(station: Station) {
        updateStationsUseCase
                .executeUpdateStation(station)
                .onEach { resource ->
                    if (resource is Resource.Success) {
                        val updatedStations =
                                state.value.stations.map { existing ->
                                    if (existing.uid == station.uid) station else existing
                                }
                        _state.value = state.value.copy(stations = updatedStations)
                    }
                }
                .launchIn(viewModelScope)
    }

    fun onEvent(event: StationsEvent) {
        when (event) {
            is StationsEvent.SearchQueryChanged -> {
                _state.value = state.value.copy(searchText = event.query)
                getStations()
            }
            is StationsEvent.ToggleSearch -> {
                val newIsSearching = !state.value.isSearching
                _state.value =
                        state.value.copy(
                                isSearching = newIsSearching,
                                searchText = if (!newIsSearching) "" else state.value.searchText
                        )
                getStations()
            }
            is StationsEvent.UpdateStation -> updateStation(event.station)
            is StationsEvent.ToggleFavorites -> {
                _state.value = state.value.copy(isFavoritesOpen = !state.value.isFavoritesOpen)
                getStations()
            }
            is StationsEvent.ChangeTab -> {
                _state.value = state.value.copy(selectedTab = event.tab)
                getStations()
            }
        }
    }
}
