package com.emrayd.sismik.data.repository

import com.emrayd.sismik.data.local.dao.EarthquakeDao
import com.emrayd.sismik.data.remote.api.EarthquakeApiService
import com.emrayd.sismik.data.remote.dto.toEntity
import com.emrayd.sismik.data.local.entity.toDomain
import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.domain.repository.EarthquakeRepository
import com.emrayd.sismik.util.Constants
import com.emrayd.sismik.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EarthquakeRepositoryImpl @Inject constructor(
    private val api: EarthquakeApiService,
    private val dao: EarthquakeDao
) : EarthquakeRepository {

    override fun getLiveEarthquakes(): Flow<Resource<List<Earthquake>>> = flow {
        emit(Resource.Loading())
        val lastCachedAt = dao.getLastCachedAt() ?: 0L
        val cacheIsStale = System.currentTimeMillis() - lastCachedAt > Constants.CACHE_EXPIRY_MS

        if (cacheIsStale) {
            try {
                val response = api.getLiveEarthquakes()
                val entities = response.result?.map { it.toEntity() } ?: emptyList()
                if (entities.isNotEmpty()) {
                    dao.deleteOlderThan(System.currentTimeMillis() - Constants.CACHE_EXPIRY_MS * 6)
                    dao.insertAll(entities)
                }
            } catch (e: Exception) {
                emit(Resource.Error("Bağlantı hatası: ${e.localizedMessage ?: "Bilinmeyen hata"}"))
                return@flow
            }
        }

        dao.observeAll().collect { entities ->
            emit(Resource.Success(entities.map { it.toDomain() }))
        }
    }

    override suspend fun refreshEarthquakes(): List<Earthquake> {
        return try {
            val response = api.getLiveEarthquakes()
            val entities = response.result?.map { it.toEntity() } ?: emptyList()
            if (entities.isNotEmpty()) {
                dao.deleteOlderThan(System.currentTimeMillis() - Constants.CACHE_EXPIRY_MS * 6)
                dao.insertAll(entities)
            }
            entities.map { it.toDomain() }
        } catch (e: Exception) { emptyList() }
    }

    override fun getArchiveEarthquakes(startDate: String, endDate: String): Flow<Resource<List<Earthquake>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getArchiveEarthquakes(startDate, endDate)
            val earthquakes = response.result?.map { it.toEntity().toDomain() } ?: emptyList()
            emit(Resource.Success(earthquakes))
        } catch (e: Exception) {
            emit(Resource.Error("Arşiv verisi yüklenemedi: ${e.localizedMessage ?: "Bilinmeyen hata"}"))
        }
    }
}