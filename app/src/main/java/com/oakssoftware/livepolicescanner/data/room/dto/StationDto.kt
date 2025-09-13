package com.oakssoftware.livepolicescanner.data.room.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oakssoftware.livepolicescanner.domain.model.Station

@Entity(tableName = "stations")
data class StationDto (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "search") val search: String,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean = false
)

fun StationDto.toStation(): Station {
    return Station(this.uid, this.name, this.location, this.search, this.isFavorite)
}