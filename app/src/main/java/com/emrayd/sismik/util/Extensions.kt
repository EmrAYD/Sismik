package com.emrayd.sismik.util

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
/**
 * Kandilli API'sinin date_time alanı için olası formatlar.
 *
 * NOT: API dokümantasyonunda tam format teyit edilemedi (geliştirme sırasında
 * web_fetch erişim hatası alındı). Bu yüzden parser, olası birkaç formatı
 * sırayla deneyip ilk eşleşeni kullanan esnek (defensive) bir yapıda yazıldı.
 * Gerçek API yanıtını test ettiğinde hangi formatın eşleştiğini görüp,
 * o formatı listenin en başına alarak performansı iyileştirebilirsin;
 * diğerlerini silmen gerekmez, zarar vermezler.
 */

private val ISTANBUL_TZ = TimeZone.getTimeZone("Europe/Istanbul")

private val READABLE_OUTPUT_FORMAT = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr")).apply {
    timeZone = ISTANBUL_TZ
}

fun Long.toReadableDate(): String {
    if (this <= 0L) return "-"
    return READABLE_OUTPUT_FORMAT.format(Date(this * 1000L))
}

private val POSSIBLE_DATE_FORMATS = listOf(
    "yyyy-MM-dd HH:mm:ss",
    "yyyy.MM.dd HH:mm:ss",
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
    "yyyy-MM-dd'T'HH:mm:ss'Z'",
    "yyyy-MM-dd'T'HH:mm:ssXXX"
)

/**
 * API'den gelen ham tarih string'ini "gg.aa.yyyy ss:dd" formatına çevirir.
 * Hiçbir format eşleşmezse boş ekran göstermemek için ham string'i olduğu gibi döner.
 */
fun String.formatReadableDate(): String {
    for (pattern in POSSIBLE_DATE_FORMATS) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                timeZone = ISTANBUL_TZ
            }
            val date = sdf.parse(this) ?: continue
            return READABLE_OUTPUT_FORMAT.format(date)
        } catch (e: Exception) {
            // Bu format eşleşmedi, sıradaki formatı dene.
        }
    }
    return this
}

/**
 * Deprem büyüklüğüne göre renk kodu döner.
 * item_earthquake.xml'de büyüklük etiketinin arka plan rengi olarak kullanılır.
 *
 * Eşikler:
 *  >= 7.0 -> kırmızı (büyük / yıkıcı)
 *  >= 5.0 -> turuncu (orta-büyük, genelde hissedilir)
 *  >= 3.0 -> sarı (hafif, bazen hissedilir)
 *  <  3.0 -> yeşil (çok hafif, genelde hissedilmez)
 */
fun magnitudeToColor(magnitude: Double): Int = when {
    magnitude >= 7.0 -> Color.parseColor("#F44336")
    magnitude >= 5.0 -> Color.parseColor("#FF9800")
    magnitude >= 3.0 -> Color.parseColor("#FFC107")
    else -> Color.parseColor("#4CAF50")
}

fun magnitudeToColor(magnitude: Float): Int = magnitudeToColor(magnitude.toDouble())