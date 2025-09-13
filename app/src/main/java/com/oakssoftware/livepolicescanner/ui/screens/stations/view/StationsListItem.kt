package com.oakssoftware.livepolicescanner.ui.screens.stations.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oakssoftware.livepolicescanner.domain.model.Station

@Composable
fun StationListItem(
    station: Station,
    onItemClick: (Station) -> Unit,
    onFavoriteButtonClick: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onItemClick(station) }
            .then(modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FavoriteButton(station, onFavoriteButtonClick)
            StationInfo(station)
        }
    }
}

@Composable
fun FavoriteButton(station: Station, onFavoriteButtonClick: (Station) -> Unit) {
    IconButton(onClick = { onFavoriteButtonClick(station) }) {
        Icon(
            imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = MaterialTheme.colorScheme.inversePrimary
        )
    }
}

@Composable
fun StationInfo(station: Station) {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            text = station.name,
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = station.location,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}