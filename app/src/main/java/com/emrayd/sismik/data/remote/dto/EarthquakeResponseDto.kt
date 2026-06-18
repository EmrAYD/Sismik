package com.emrayd.sismik.data.remote.dto

import com.emrayd.sismik.data.local.entity.EarthquakeEntity
import com.google.gson.annotations.SerializedName

/**
 * Kandilli Rasathanesi API'sinin (api.orhanaydogdu.com.tr) hem "live" hem "archive"
 * endpoint'leri aynı response şeklini kullanır, bu yüzden tek bir DTO seti ikisine de yeter.
 *
 * NOT: API dokümantasyonunda tam alan adları/iç içe yapı doğrulanamadı (geliştirme
 * sırasında dokümantasyon sayfasına erişimde hata alındı). Bu yüzden tüm alanlar
 * nullable tanımlandı ve toEntity() mapper'ında güvenli (defensive) varsayılan
 * değerler kullanıldı; API beklenenden farklı/eksik bir alan gönderse bile uygulama
 * çökmez. Gerçek API yanıtını incelediğinde @SerializedName değerlerini buna göre
 * düzeltmen gerekebilir.
 */
data class EarthquakeResponseDto(
    val status: Boolean? = null,
    val result: List<EarthquakeItemDto>? = null
)

data class EarthquakeItemDto(
    @SerializedName("earthquake_id") val earthquakeId: String? = null,
    val title: String? = null,
    @SerializedName("date_time") val dateTime: String? = null,
    @SerializedName("created_at") val createdAt: Long? = null,
    val mag: Double? = null,
    val depth: Double? = null,
    val geojson: GeoJsonDto? = null,
    @SerializedName("location_properties") val locationProperties: LocationPropertiesDto? = null,
    val provider: String? = null
)

data class GeoJsonDto(
    val type: String? = null,
    val coordinates: List<Double>? = null
)

data class LocationPropertiesDto(
    @SerializedName("closestCity") val closestCity: ClosestCityDto? = null
)

data class ClosestCityDto(
    val name: String? = null,
    val distance: Double? = null
)

/**
 * DTO -> Room Entity dönüşümü. API'den gelen ham veriyi veritabanına yazılabilecek
 * formata çevirir; eksik/null alanlar için güvenli varsayılan değerler kullanılır.
 */
fun EarthquakeItemDto.toEntity(): EarthquakeEntity {
    val longitude = geojson?.coordinates?.getOrNull(0) ?: 0.0
    val latitude = geojson?.coordinates?.getOrNull(1) ?: 0.0
    val distanceKm = (locationProperties?.closestCity?.distance ?: 0.0) / 1000.0

    return EarthquakeEntity(
        id = earthquakeId ?: dateTime ?: System.currentTimeMillis().toString(),
        title = title ?: "Bilinmeyen Konum",
        magnitude = mag ?: 0.0,
        depth = depth ?: 0.0,
        latitude = latitude,
        longitude = longitude,
        closestCity = locationProperties?.closestCity?.name ?: "-",
        closestCityDistanceKm = distanceKm,
        dateTime = dateTime ?: "",
        epochSeconds = createdAt ?: 0L,
        provider = provider ?: "-"
    )
}