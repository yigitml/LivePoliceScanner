package com.oakssoftware.livepolicescanner.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.oakssoftware.livepolicescanner.data.room.dto.StationDto

@Database(
    entities = [StationDto::class],
    version = 1,
    exportSchema = false
)
abstract class StationDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}