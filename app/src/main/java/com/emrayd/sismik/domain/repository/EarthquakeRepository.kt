package com.emrayd.sismik.domain.repository

import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.util.Resource
import kotlinx.coroutines.flow.Flow

interface EarthquakeRepository {
    fun getLiveEarthquakes(): Flow<Resource<List<Earthquake>>>
    fun getArchiveEarthquakes(startDate: String, endDate: String): Flow<Resource<List<Earthquake>>>
    suspend fun refreshEarthquakes()
}