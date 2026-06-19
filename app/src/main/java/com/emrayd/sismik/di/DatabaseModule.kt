package com.emrayd.sismik.di

import android.content.Context
import androidx.room.Room
import com.emrayd.sismik.data.local.dao.EarthquakeDao
import com.emrayd.sismik.data.local.database.EarthquakeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Yerel veri katmanının bağımlılıklarını sağlayan Hilt modülü.
 *
 * Room veritabanı yalnızca bir kez oluşturulmalıdır (singleton); aksi hâlde
 * aynı dosyaya birden fazla bağlantı açılır ve veri tutarsızlıkları oluşabilir.
 * DataStore da benzer şekilde uygulama genelinde tek örnekle çalışır.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEarthquakeDatabase(@ApplicationContext context: Context): EarthquakeDatabase {
        return Room.databaseBuilder(
            context,
            EarthquakeDatabase::class.java,
            "sismik_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEarthquakeDao(database: EarthquakeDatabase): EarthquakeDao {
        return database.earthquakeDao()
    }
}