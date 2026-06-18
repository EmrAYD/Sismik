package com.emrayd.sismik.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emrayd.sismik.data.local.entity.EarthquakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EarthquakeDao {

    @Query("SELECT * FROM earthquakes ORDER BY epochSeconds DESC")
    fun observeAll(): Flow<List<EarthquakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(earthquakes: List<EarthquakeEntity>)

    @Query("DELETE FROM earthquakes WHERE cachedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)

    @Query("SELECT * FROM earthquakes ORDER BY epochSeconds DESC LIMIT 1")
    suspend fun getLatest(): EarthquakeEntity?

    @Query("SELECT MAX(cachedAt) FROM earthquakes")
    suspend fun getLastCachedAt(): Long?
}