package com.emrayd.sismik.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emrayd.sismik.data.local.dao.EarthquakeDao
import com.emrayd.sismik.data.local.entity.EarthquakeEntity

@Database(
    entities = [EarthquakeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class EarthquakeDatabase : RoomDatabase() {
    abstract fun earthquakeDao(): EarthquakeDao
}