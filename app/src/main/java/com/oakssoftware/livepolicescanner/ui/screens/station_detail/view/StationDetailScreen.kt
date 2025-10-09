package com.oakssoftware.livepolicescanner.ui.screens.station_detail.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oakssoftware.livepolicescanner.R
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.ui.ScreenState
import com.oakssoftware.livepolicescanner.ui.components.BannerAd
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.MediaState
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.PlayerState
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.StationDetailEvent
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.StationDetailViewModel
import com.oakssoftware.livepolicescanner.ui.screens.stations.view.ErrorContent
import com.oakssoftware.livepolicescanner.ui.screens.stations.view.LoadingContent
import com.oakssoftware.livepolicescanner.util.Constants

@Composable
fun StationDetailScreen(
    ip: PaddingValues,
    viewModel: StationDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val mediaState = viewModel.mediaPlayerState.value

    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.padding(ip)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray)
                )
                BannerAd(Modifier.fillMaxWidth().padding(4.dp), adUnitId = Constants.BANNER_DETAIL)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray)
                )
            }        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(ip)
                .padding(innerPadding)
        ) {
            when (state.screenState) {
                ScreenState.Success -> {
                    state.station?.let { station ->
                        StationDetailsContent(station, viewModel, mediaState)
                    } ?: run {
                        ErrorContent("There was an error getting the station")
                    }
                }

                ScreenState.Error -> {
                    ErrorContent("There was an error getting the station")
                }

                ScreenState.Loading -> {
                    LoadingContent()
                }
            }
        }
    }
}

@Composable
fun StationDetailsContent(
    station: Station,
    viewModel: StationDetailViewModel,
    mediaState: MediaState
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 64.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //StationImageCard(station, modifier = Modifier.weight(0.615f))
            StationControlsCard(station, viewModel, Modifier, mediaState)
        }
    }
}

@Composable
fun StationImageCard(station: Station, modifier: Modifier) {
    ElevatedCard(
        modifier = Modifier
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
            .fillMaxWidth()
            .then(modifier),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.front),
                contentDescription = "App Icon",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .size(120.dp)
                    .border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary), CircleShape)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(216.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )

            Text(
                text = station.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = station.location,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )
        }
    }
}

@Composable
fun StationControlsCard(
    station: Station,
    viewModel: StationDetailViewModel,
    modifier: Modifier,
    mediaState: MediaState
) {
    ElevatedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .then(modifier),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = station.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = station.location,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )

            MediaControlPanel(viewModel, mediaState, station)
        }
    }
}

@Composable
fun MediaControlPanel(viewModel: StationDetailViewModel, mediaState: MediaState, station: Station) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (mediaState.isConnectionEstablished) {
            true -> {
                InformationText("Connection established", color = Color.Green)
            }

            false -> {
                InformationText("Press play to connect")
            }
        }

        Spacer(Modifier.padding(12.dp))

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MediaControlButton(R.drawable.baseline_play_24, "Start Button", modifier = Modifier) {
                val url = "https://broadcastify.cdnstream1.com/${station.uid}"
                viewModel.onEvent(
                    StationDetailEvent.UpdatePlayer(
                        StationDetailEvent.MediaPlayerActions.PLAY,
                        url = url
                    )
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            MediaControlButton(R.drawable.baseline_pause_24, "Pause Button", modifier = Modifier) {
                viewModel.onEvent(
                    StationDetailEvent.UpdatePlayer(StationDetailEvent.MediaPlayerActions.PAUSE)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            MediaControlButton(R.drawable.baseline_stop_24, "Stop Button", modifier = Modifier) {
                viewModel.onEvent(
                    StationDetailEvent.UpdatePlayer(StationDetailEvent.MediaPlayerActions.STOP)
                )
            }
        }

        Spacer(Modifier.padding(8.dp))

        when (mediaState.playerState) {
            PlayerState.IDLE -> {
                InformationText("Idle")
            }

            PlayerState.PLAYING -> {
                InformationText("Playing")
            }

            PlayerState.PAUSED -> {
                InformationText("Paused")
            }

            PlayerState.STOPPED -> {
                InformationText("Stopped")
            }
        }
    }
}

@Composable
fun MediaControlButton(resId: Int, contentDescription: String, modifier: Modifier, onClick: () -> Unit,) {
    FloatingActionButton(
        shape = AbsoluteRoundedCornerShape(216.dp),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(resId),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(32.dp)
                .then(modifier)
        )
    }
}

@Composable
fun InformationText(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
    Text(text = text, modifier = Modifier.then(modifier), color = color)
}

@Composable
fun ErrorContent(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LoadingContent() {
    CircularProgressIndicator()
}