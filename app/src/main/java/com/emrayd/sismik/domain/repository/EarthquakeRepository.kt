package com.emrayd.sismik.domain.repository

import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Data katmanının domain'e sunduğu sözleşme (contract).
 * ViewModel'ler bu interface'e bağımlı olur, EarthquakeRepositoryImpl'i hiç bilmez.
 * Bu sayede ileride veri kaynağı değişse (örn. başka bir API eklense) ViewModel kodu değişmez.
 */
interface EarthquakeRepository {

    /**
     * Önce yerel veritabanındaki (cache) veriyi yayınlar, ardından ağdan güncel
     * veri çekip veritabanını günceller ve tekrar yayınlar (cache-then-network pattern).
     */
    fun getLiveEarthquakes(): Flow<Resource<List<Earthquake>>>

    /**
     * WorkManager'ın arka planda bildirim kontrolü için kullandığı, tek seferlik
     * (Flow olmayan) basit network çağrısı.
     */
    suspend fun refreshEarthquakes(): List<Earthquake>

    /**
     * Belirli bir tarih aralığındaki depremleri Kandilli'nin "archive" endpoint'inden
     * çeker. Şu an aktif olarak kullanılan bir ekran yok; ileride eklenebilecek bir
     * istatistik/geçmiş ekranı için altyapı olarak hazırlandı. Bu yüzden cache'e
     * yazılmaz, doğrudan ağdan okunup yayınlanır.
     */
    fun getArchiveEarthquakes(startDate: String, endDate: String): Flow<Resource<List<Earthquake>>>
}