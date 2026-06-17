package com.emrayd.sismik.domain.usecase

import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.domain.repository.EarthquakeRepository
import com.emrayd.sismik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Tek sorumluluk: canlı deprem listesini repository'den alıp olduğu gibi ViewModel'e iletmek.
 * Şu an repository'yi tek satırda çağırmaktan ibaret görünse de, ileride burada iş kuralı
 * eklenmesi gerekirse (örn. belirli bir sağlayıcıyı hariç tutma) ViewModel'e dokunmadan
 * sadece bu sınıf değiştirilir.
 */
class GetLiveEarthquakesUseCase @Inject constructor(
    private val repository: EarthquakeRepository
) {
    operator fun invoke(): Flow<Resource<List<Earthquake>>> = repository.getLiveEarthquakes()
}