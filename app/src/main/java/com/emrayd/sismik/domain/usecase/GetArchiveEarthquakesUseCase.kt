package com.emrayd.sismik.domain.usecase

import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.domain.repository.EarthquakeRepository
import com.emrayd.sismik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Belirli bir tarih aralığındaki depremleri Kandilli'nin "archive" endpoint'inden çeker.
 *
 * NOT: Sismik'in güncel ekran listesinde (Şehrim, Akış, Detay, Düdük, Bilgi, Ayarlar) bu
 * use case'i çağıran bir ekran şu an yok — istatistik grafiği ekranı kapsam dışı bırakıldı.
 * Bu sınıf ileride bir "geçmiş depremler" veya istatistik ekranı eklenmek istenirse hazır
 * altyapı olarak bırakıldı.
 *
 * @param startDate Başlangıç tarihi, "YYYY-MM-DD" formatında.
 * @param endDate Bitiş tarihi, "YYYY-MM-DD" formatında.
 */
class GetArchiveEarthquakesUseCase @Inject constructor(
    private val repository: EarthquakeRepository
) {
    operator fun invoke(startDate: String, endDate: String): Flow<Resource<List<Earthquake>>> =
        repository.getArchiveEarthquakes(startDate, endDate)
}