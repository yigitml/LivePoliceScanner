package com.oakssoftware.livepolicescanner.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oakssoftware.livepolicescanner.data.room.dto.StationDto

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg station: StationDto)

    @Query("SELECT * FROM stations")
    suspend fun getStationsFromCache(): List<StationDto>

    @Query("SELECT * FROM stations WHERE uid == :uid")
    suspend fun getStationDetails(uid: Int): StationDto

    @Update
    suspend fun updateStation(station: StationDto)

    @Query("SELECT * FROM stations WHERE isFavorite == 1")
    suspend fun getFavoriteStations(): List<StationDto>
}