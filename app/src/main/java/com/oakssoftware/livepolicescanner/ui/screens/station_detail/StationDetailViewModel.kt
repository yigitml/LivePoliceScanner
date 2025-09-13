package com.oakssoftware.livepolicescanner.ui.screens.station_detail

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.domain.use_case.get_station_detail.GetStationDetailUseCase
import com.oakssoftware.livepolicescanner.domain.use_case.get_stations.GetStationsUseCase
import com.oakssoftware.livepolicescanner.domain.use_case.update_stations.UpdateStationsUseCase
import com.oakssoftware.livepolicescanner.ui.ScreenState
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

    private val _mediaState = mutableStateOf(MediaState(PlayerState.IDLE, false))
    val mediaPlayerState: State<MediaState> = _mediaState

    private var mediaPlayer: MediaPlayer? = null

    init {
        stateHandle.get<String>(Constants.STATION_ID)?.toIntOrNull()?.let { stationId ->
            getStationDetail(stationId)
            getFavoriteStations()
        } ?: run {
            _state.value = StationDetailState(
                screenState = ScreenState.Error,
                errorMessage = "Error getting station detail"
            )
        }
    }

    private fun handleStationsResource(resource: Resource<List<Station>>) {
        _state.value = state.value.copy(
            favoriteStations = when (resource) {
                is Resource.Success -> resource.data ?: emptyList()
                else -> emptyList()
            },
            errorMessage = resource.message
        )
    }

    private fun handleStationResource(resource: Resource<Station>) {
        _state.value = state.value.copy(
            screenState = when (resource) {
                is Resource.Success -> ScreenState.Success
                is Resource.Loading -> ScreenState.Loading
                is Resource.Error -> ScreenState.Error
            },
            station = resource.data,
            errorMessage = resource.message
        )
    }

    private fun getStationDetail(stationId: Int) {
        getStationDetailUseCase.executeGetStationDetail(stationId)
            .onEach { handleStationResource(it) }
            .launchIn(viewModelScope)
    }

    private fun getFavoriteStations() {
        getStationsUseCase.executeGetStations(isFavToggleOpen = true)
            .onEach { handleStationsResource(it) }
            .launchIn(viewModelScope)
    }

    private fun updateStation(station: Station) {
        updateStationsUseCase.executeUpdateStation(station)
            .onEach { handleStationResource(it) }
            .launchIn(viewModelScope)
    }

    private fun playMedia(url: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    _mediaState.value = MediaState(PlayerState.PLAYING, true)
                }
            }
        } else {
            if (_mediaState.value.playerState == PlayerState.PAUSED) {
                mediaPlayer?.start()
                _mediaState.value =
                    MediaState(PlayerState.PLAYING, _mediaState.value.isConnectionEstablished)
            }
        }
    }

    private fun pauseMedia() {
        if (_mediaState.value.playerState == PlayerState.PLAYING) {
            mediaPlayer?.pause()
            _mediaState.value =
                MediaState(PlayerState.PAUSED, _mediaState.value.isConnectionEstablished)
        }
    }

    private fun stopMedia() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _mediaState.value =
            MediaState(PlayerState.STOPPED, _mediaState.value.isConnectionEstablished)
    }

    fun onEvent(event: StationDetailEvent) {
        when (event) {
            is StationDetailEvent.UpdateStation -> updateStation(event.station)
            is StationDetailEvent.UpdatePlayer -> {
                when (event.player) {
                    StationDetailEvent.MediaPlayerActions.PLAY -> playMedia(url = event.url!!)
                    StationDetailEvent.MediaPlayerActions.PAUSE -> pauseMedia()
                    StationDetailEvent.MediaPlayerActions.STOP -> stopMedia()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }
}