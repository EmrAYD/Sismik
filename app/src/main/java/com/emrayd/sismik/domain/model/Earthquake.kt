package com.emrayd.sismik.domain.model

/**
 * Uygulama içinde kullanılan, API/DB detaylarından bağımsız temiz domain modeli.
 * UI ve use case'ler sadece bu sınıfı bilir; Retrofit DTO'su veya Room Entity'si
 * domain katmanına hiç sızmaz (Clean Architecture'ın temel kuralı: bağımlılık
 * her zaman dışarıdan içeriye, yani data -> domain yönünde olur).
 */
data class Earthquake(
    val id: String,
    val title: String,
    val magnitude: Double,
    val depth: Double,
    val latitude: Double,
    val longitude: Double,
    val closestCity: String,
    val closestCityDistance: Double,
    val dateTime: String,
    val provider: String
)