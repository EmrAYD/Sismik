package com.emrayd.sismik.data.remote.api

import com.emrayd.sismik.data.remote.dto.EarthquakeResponseDto
import com.emrayd.sismik.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeApiService {

    @GET("deprem/kandilli/live")
    suspend fun getLiveEarthquakes(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = Constants.DEFAULT_FETCH_LIMIT
    ): EarthquakeResponseDto

    @GET("deprem/kandilli/archive")
    suspend fun getArchiveEarthquakes(
        @Query("start") startDate: String,
        @Query("end") endDate: String,
        @Query("limit") limit: Int = Constants.DEFAULT_FETCH_LIMIT
    ): EarthquakeResponseDto
}
