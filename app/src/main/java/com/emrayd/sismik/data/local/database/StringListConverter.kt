package com.emrayd.sismik.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room, List<String> tipini doğrudan SQLite'a yazamaz.
 * Bu TypeConverter listeyi JSON string'e çevirip saklamamızı sağlar.
 * EarthquakeDatabase sınıfına @TypeConverters(StringListConverter::class) ile eklenir.
 */
class StringListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<String>): String = gson.toJson(list)

    @TypeConverter
    fun toList(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}