package com.emrayd.sismik.domain.usecase

import com.emrayd.sismik.domain.model.Earthquake
import javax.inject.Inject

/**
 * Akış (Feed) ekranındaki sıralama ve şehir arama mantığını barındırır.
 * ViewModel, kullanıcı bir sıralama seçeneğine bastığında veya arama kutusuna
 * yazdığında elindeki ham listeyi bu use case'den geçirip filtrelenmiş/sıralanmış
 * sonucu alır. UI hiçbir filtreleme/sıralama mantığı içermez.
 */
class FilterEarthquakesUseCase @Inject constructor() {

    enum class SortType {
        MAGNITUDE_DESC,  // Şiddeti yüksekten aza
        MAGNITUDE_ASC,   // Şiddeti azdan yükseğe
        DATE_DESC        // En yeniden eskiye
    }

    operator fun invoke(
        earthquakes: List<Earthquake>,
        sortType: SortType,
        cityQuery: String = ""
    ): List<Earthquake> {
        val filtered = if (cityQuery.isBlank()) {
            earthquakes
        } else {
            earthquakes.filter { earthquake ->
                earthquake.closestCity.contains(cityQuery, ignoreCase = true) ||
                        earthquake.title.contains(cityQuery, ignoreCase = true)
            }
        }

        return when (sortType) {
            SortType.MAGNITUDE_DESC -> filtered.sortedByDescending { it.magnitude }
            SortType.MAGNITUDE_ASC -> filtered.sortedBy { it.magnitude }
            // NOT: dateTime "yyyy.MM.dd HH:mm:ss" gibi sıfır dolgulu (zero-padded) bir
            // formatta geldiği sürece string karşılaştırması doğru kronolojik sıralama
            // verir. API'nin gerçek formatı doğrulandığında bu varsayım kontrol edilmeli.
            SortType.DATE_DESC -> filtered.sortedByDescending { it.dateTime }
        }
    }
}