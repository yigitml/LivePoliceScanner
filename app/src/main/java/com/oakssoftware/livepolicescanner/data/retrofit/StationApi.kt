package com.oakssoftware.livepolicescanner.data.retrofit

import com.oakssoftware.livepolicescanner.data.retrofit.dto.StationsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApi {

    @GET("live-police-scanner-0.appspot.com/o/data.json")
    suspend fun getStations(
        @Query("alt") query: String = "media",
    ): StationsDto
}