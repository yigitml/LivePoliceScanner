package com.oakssoftware.livepolicescanner.ui.screens.station_detail.view

import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oakssoftware.livepolicescanner.R
import com.oakssoftware.livepolicescanner.domain.model.Station
import com.oakssoftware.livepolicescanner.ui.ScreenState
import com.oakssoftware.livepolicescanner.ui.components.BannerAd
import com.oakssoftware.livepolicescanner.ui.components.ShowInterstitialAd
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.ConnectionQuality
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.MediaState
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.PlayerState
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.StationDetailEvent
import com.oakssoftware.livepolicescanner.ui.screens.station_detail.StationDetailViewModel
import com.oakssoftware.livepolicescanner.util.Constants
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationDetailScreen(ip: PaddingValues, viewModel: StationDetailViewModel = hiltViewModel()) {
        val state = viewModel.state.value
        val mediaState = viewModel.mediaPlayerState.value
        val activity = LocalActivity.current
        var shouldShowAd by remember { mutableStateOf(false) }

        // Check if we should show the interstitial ad after a brief delay
        LaunchedEffect(Unit) {
                delay(500) // Let user see the screen first (AdMob compliant)
                if (viewModel.shouldShowInterstitialAd()) {
                        shouldShowAd = true
                }
        }

        // Show interstitial ad if needed
        if (shouldShowAd) {
                ShowInterstitialAd(
                        activity = activity,
                        adUnitId = Constants.INTERSTITIAL_MAIN,
                        shouldShow = shouldShowAd,
                        onAdDismissed = {
                                viewModel.markInterstitialAdShown()
                                shouldShowAd = false
                        },
                        onAdFailed = {
                                // Mark as shown even on failure to avoid repeated attempts
                                viewModel.markInterstitialAdShown()
                                shouldShowAd = false
                        }
                )
        }

        Scaffold(
                topBar = {
                        state.station?.let { station ->
                                TopAppBar(
                                        title = { Text("Station Detail") },
                                        actions = {
                                                IconButton(
                                                        onClick = {
                                                                viewModel.onEvent(
                                                                        StationDetailEvent
                                                                                .UpdateStation(
                                                                                        station.copy(
                                                                                                isFavorite =
                                                                                                        !station.isFavorite
                                                                                        )
                                                                                )
                                                                )
                                                        }
                                                ) {
                                                        Icon(
                                                                painter =
                                                                        painterResource(
                                                                                if (station.isFavorite
                                                                                )
                                                                                        R.drawable
                                                                                                .baseline_favorite_24
                                                                                else
                                                                                        R.drawable
                                                                                                .outline_favorite_24
                                                                        ),
                                                                contentDescription =
                                                                        if (station.isFavorite)
                                                                                "Remove from favorites"
                                                                        else "Add to favorites",
                                                                tint =
                                                                        if (station.isFavorite)
                                                                                Color.Red
                                                                        else
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSurface
                                                        )
                                                }
                                        },
                                        colors =
                                                TopAppBarDefaults.topAppBarColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.surface
                                                )
                                )
                        }
                },
                bottomBar = {
                        BannerAd(
                                Modifier.fillMaxWidth().padding(ip).navigationBarsPadding(),
                                adUnitId = Constants.BANNER_DETAIL
                        )
                }
        ) { innerPadding ->
                Surface(modifier = Modifier.fillMaxSize().padding(ip).padding(innerPadding)) {
                        when (state.screenState) {
                                ScreenState.Success -> {
                                        state.station?.let { station ->
                                                StationDetailsContent(
                                                        station,
                                                        viewModel,
                                                        mediaState
                                                )
                                        }
                                                ?: run {
                                                        EnhancedErrorContent(
                                                                "There was an error getting the station"
                                                        )
                                                }
                                }
                                ScreenState.Error -> {
                                        EnhancedErrorContent(
                                                "There was an error getting the station"
                                        )
                                }
                                ScreenState.Loading -> {
                                        EnhancedLoadingContent("Loading station details...")
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                                modifier =
                                        Modifier.widthIn(max = 600.dp)
                                                .verticalScroll(rememberScrollState())
                                                .padding(bottom = 64.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) { StationControlsCard(station, viewModel, Modifier, mediaState) }
                }
        }
}

@Composable
fun StationImageCard(station: Station, modifier: Modifier) {
        ElevatedCard(
                modifier =
                        Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
                                .fillMaxWidth()
                                .then(modifier),
                shape = MaterialTheme.shapes.large,
                colors =
                        CardDefaults.cardColors()
                                .copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(
                                                top = 32.dp,
                                                start = 16.dp,
                                                end = 16.dp,
                                                bottom = 8.dp
                                        ),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Image(
                                painter = painterResource(id = R.drawable.front),
                                contentDescription = "App Icon",
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                                modifier =
                                        Modifier.size(120.dp)
                                                .border(
                                                        BorderStroke(
                                                                4.dp,
                                                                MaterialTheme.colorScheme.primary
                                                        ),
                                                        CircleShape
                                                )
                                                .padding(4.dp)
                                                .clip(RoundedCornerShape(216.dp))
                                                .background(
                                                        MaterialTheme.colorScheme.surfaceContainer
                                                )
                        )

                        Text(
                                text = station.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 32.dp).fillMaxWidth(),
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
                modifier = Modifier.padding(24.dp).fillMaxWidth().then(modifier),
                shape = MaterialTheme.shapes.large,
                colors =
                        CardDefaults.cardColors()
                                .copy(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(
                                                top = 24.dp,
                                                start = 24.dp,
                                                end = 24.dp,
                                                bottom = 24.dp
                                        ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                                text = station.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = FontWeight.Bold
                        )

                        Text(
                                text = station.location,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 2
                        )

                        Spacer(Modifier.height(24.dp))

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
                // Feature 1: Enhanced connection status with color-coding
                EnhancedConnectionStatus(mediaState)

                Spacer(Modifier.height(16.dp))

                // Feature 2: Larger, more accessible media control buttons (64dp)
                Row(
                        modifier = Modifier.padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        if (mediaState.playerState == PlayerState.LOADING) {
                                Box(
                                        modifier = Modifier.size(64.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(48.dp),
                                                strokeWidth = 4.dp
                                        )
                                }
                        } else {
                                LargeMediaControlButton(
                                        R.drawable.baseline_play_24,
                                        "Play",
                                        enabled = mediaState.playerState != PlayerState.PLAYING
                                ) {
                                        val url =
                                                "https://broadcastify.cdnstream1.com/${station.uid}"
                                        viewModel.onEvent(
                                                StationDetailEvent.UpdatePlayer(
                                                        StationDetailEvent.MediaPlayerActions.PLAY,
                                                        url = url
                                                )
                                        )
                                }
                        }

                        LargeMediaControlButton(
                                R.drawable.baseline_pause_24,
                                "Pause",
                                enabled = mediaState.playerState == PlayerState.PLAYING
                        ) {
                                viewModel.onEvent(
                                        StationDetailEvent.UpdatePlayer(
                                                StationDetailEvent.MediaPlayerActions.PAUSE
                                        )
                                )
                        }

                        LargeMediaControlButton(
                                R.drawable.baseline_stop_24,
                                "Stop",
                                enabled =
                                        mediaState.playerState != PlayerState.IDLE &&
                                                mediaState.playerState != PlayerState.STOPPED
                        ) {
                                viewModel.onEvent(
                                        StationDetailEvent.UpdatePlayer(
                                                StationDetailEvent.MediaPlayerActions.STOP
                                        )
                                )
                        }
                }

                Spacer(Modifier.height(16.dp))

                // Feature 3: Listening duration timer
                if (mediaState.playerState == PlayerState.PLAYING ||
                                mediaState.playerState == PlayerState.PAUSED
                ) {
                        ListeningDurationDisplay(mediaState.listeningDurationSeconds)
                        Spacer(Modifier.height(8.dp))
                }

                // Feature 4: Better loading states with progress indication
                EnhancedPlayerStateDisplay(mediaState.playerState)
        }
}

// Feature 1: Enhanced connection status with color-coding
@Composable
fun EnhancedConnectionStatus(mediaState: MediaState) {
        val (statusText, statusColor) =
                when {
                        !mediaState.isConnectionEstablished ->
                                "Not Connected" to MaterialTheme.colorScheme.onSurfaceVariant
                        else ->
                                when (mediaState.connectionQuality) {
                                        ConnectionQuality.EXCELLENT ->
                                                "🟢 Connected - Excellent Quality" to
                                                        Color(0xFF4CAF50)
                                        ConnectionQuality.GOOD ->
                                                "🟡 Connected - Good Quality" to Color(0xFFFFC107)
                                        ConnectionQuality.FAIR ->
                                                "🟠 Connected - Fair Quality" to Color(0xFFFF9800)
                                        ConnectionQuality.POOR ->
                                                "🔴 Connected - Poor Quality" to Color(0xFFF44336)
                                        ConnectionQuality.UNKNOWN ->
                                                "⚪ Connected" to MaterialTheme.colorScheme.primary
                                }
                }

        ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
                Text(
                        text = statusText,
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                )
        }
}

// Feature 2: Larger, more accessible media control buttons (64dp minimum touch target)
@Composable
fun LargeMediaControlButton(
        resId: Int,
        contentDescription: String,
        enabled: Boolean = true,
        onClick: () -> Unit,
) {
        FloatingActionButton(
                onClick = onClick,
                modifier = Modifier.size(64.dp), // Meets 48dp minimum, exceeds for better UX
                shape = CircleShape,
                containerColor =
                        if (enabled) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                contentColor =
                        if (enabled) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
                Icon(
                        painter = painterResource(resId),
                        contentDescription = contentDescription,
                        modifier = Modifier.size(32.dp)
                )
        }
}

// Feature 3: Listening duration timer display
@Composable
fun ListeningDurationDisplay(durationSeconds: Long) {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60

        val timeString =
                if (hours > 0) {
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                        String.format("%02d:%02d", minutes, seconds)
                }

        ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
        ) {
                Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Text(
                                text = "Listening Duration",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                                text = timeString,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Bold
                        )
                }
        }
}

// Feature 4: Better loading states with progress indication
@Composable
fun EnhancedPlayerStateDisplay(playerState: PlayerState) {
        val (stateText, stateColor) =
                when (playerState) {
                        PlayerState.IDLE -> "Ready to Play" to MaterialTheme.colorScheme.onSurface
                        PlayerState.LOADING ->
                                "Buffering Stream..." to MaterialTheme.colorScheme.primary
                        PlayerState.PLAYING -> "🎵 Now Playing" to Color(0xFF4CAF50)
                        PlayerState.PAUSED -> "⏸ Paused" to Color(0xFFFFC107)
                        PlayerState.STOPPED ->
                                "⏹ Stopped" to MaterialTheme.colorScheme.onSurfaceVariant
                }

        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
        ) {
                Text(
                        text = stateText,
                        style = MaterialTheme.typography.titleMedium,
                        color = stateColor,
                        fontWeight = FontWeight.Medium
                )

                if (playerState == PlayerState.LOADING) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(0.6f),
                                color = MaterialTheme.colorScheme.primary
                        )
                }
        }
}

// Feature 4: Enhanced loading content
@Composable
fun EnhancedLoadingContent(message: String = "Loading...") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp
                        )
                        Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                }
        }
}

// Feature 4: Enhanced error content
@Composable
fun EnhancedErrorContent(message: String) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ElevatedCard(
                        modifier = Modifier.padding(24.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                ) {
                        Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        painter = painterResource(R.drawable.baseline_stop_24),
                                        contentDescription = "Error",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                        text = "Error",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        textAlign = TextAlign.Center
                                )
                        }
                }
        }
}

@Composable
fun MediaControlButton(
        resId: Int,
        contentDescription: String,
        modifier: Modifier,
        onClick: () -> Unit,
) {
        FloatingActionButton(shape = AbsoluteRoundedCornerShape(216.dp), onClick = onClick) {
                Icon(
                        painter = painterResource(resId),
                        contentDescription = contentDescription,
                        modifier = Modifier.size(32.dp).then(modifier)
                )
        }
}

@Composable
fun InformationText(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSurface
) {
        Text(text = text, modifier = Modifier.then(modifier), color = color)
}
