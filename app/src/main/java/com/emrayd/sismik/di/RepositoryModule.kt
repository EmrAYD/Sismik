package com.emrayd.sismik.di

import com.emrayd.sismik.data.repository.EarthquakeRepositoryImpl
import com.emrayd.sismik.domain.repository.EarthquakeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
// NOT: @Binds için modül abstract sınıf olmalıdır (object değil)
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEarthquakeRepository(
        impl: EarthquakeRepositoryImpl
    ): EarthquakeRepository
}