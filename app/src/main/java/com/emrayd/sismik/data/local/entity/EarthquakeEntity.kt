package com.emrayd.sismik.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emrayd.sismik.domain.model.Earthquake

/**
 * Room veritabanı tablosu. API'den gelen veriyi yerelde saklamak için kullanılır;
 * offline-first deneyimin (cache-then-network pattern'in "cache" kısmı) temelini oluşturur.
 */
@Entity(tableName = "earthquakes")
data class EarthquakeEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val magnitude: Double,
    val depth: Double,
    val latitude: Double,
    val longitude: Double,
    val closestCity: String,
    val closestCityDistanceKm: Double,
    val dateTime: String,
    val epochSeconds: Long,
    val provider: String,
    val cachedAt: Long = System.currentTimeMillis()
)

/**
 * Room Entity -> Domain modeli dönüşümü.
 * cachedAt domain modelinde yer almaz, çünkü UI'ı ilgilendirmeyen bir altyapı detayıdır.
 */
fun EarthquakeEntity.toDomain(): Earthquake = Earthquake(
    id = id,
    title = title,
    magnitude = magnitude,
    depth = depth,
    latitude = latitude,
    longitude = longitude,
    closestCity = closestCity,
    closestCityDistanceKm = closestCityDistanceKm,
    dateTime = dateTime,
    epochSeconds = epochSeconds,
    provider = provider
)