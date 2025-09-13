package com.oakssoftware.livepolicescanner.dependency_injection

import com.oakssoftware.livepolicescanner.data.repository.StationRepositoryImpl
import com.oakssoftware.livepolicescanner.data.retrofit.StationApi
import com.oakssoftware.livepolicescanner.data.room.StationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesStationRepository(
        stationApi: StationApi,
        stationDao: StationDao,
    ): StationRepositoryImpl =
        StationRepositoryImpl(
            stationApi = stationApi,
            stationDao = stationDao,
        )
}