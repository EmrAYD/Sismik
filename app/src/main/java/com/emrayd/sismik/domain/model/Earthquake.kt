package com.emrayd.sismik.domain.model

data class Earthquake (
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