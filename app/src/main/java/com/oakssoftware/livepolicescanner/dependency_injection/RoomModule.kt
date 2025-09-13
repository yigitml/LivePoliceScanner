package com.oakssoftware.livepolicescanner.dependency_injection

import android.content.Context
import androidx.room.Room
import com.oakssoftware.livepolicescanner.data.room.StationDao
import com.oakssoftware.livepolicescanner.data.room.StationDatabase
import com.oakssoftware.livepolicescanner.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun providesStationDatabase(@ApplicationContext context: Context): StationDatabase =
        Room.databaseBuilder(
            context,
            StationDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigrationFrom().build()

    @Provides
    @Singleton
    fun providesStationDao(stationDatabase: StationDatabase): StationDao =
        stationDatabase.stationDao()
}