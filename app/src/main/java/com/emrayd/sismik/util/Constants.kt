package com.emrayd.sismik.util

object Constants {
    // Kandilli Rasathanesi API - base url
    const val BASE_URL = "https://api.orhanaydogdu.com.tr/"

    // Bildirim kanalı
    const val NOTIFICATION_CHANNEL_ID = "sismik_earthquake_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Deprem Bildirimleri"
    const val NOTIFICATION_CHANNEL_DESCRIPTION =
        "Belirlediğiniz eşik değerini aşan yeni depremler için bildirim gönderir"

    // Yerel veritabanındaki verinin "taze" sayılma süresi (5 dakika).
    // Bu süre geçmeden tekrar ekrana girilirse network'e gitmeden cache gösterilir.
    const val CACHE_EXPIRY_MS = 5 * 60 * 1000L

    // WorkManager periyodik kontrol ayarları.
    // Not: PeriodicWorkRequest için Android'in izin verdiği minimum aralık 15 dakikadır.
    const val NOTIFICATION_CHECK_INTERVAL_MINUTES = 15L
    const val NOTIFICATION_WORK_NAME = "sismik_earthquake_check"

    // API sayfalama - tek seferde çekilecek kayıt sayısı
    const val DEFAULT_FETCH_LIMIT = 50

    // DataStore preferences dosya adı
    const val SETTINGS_DATASTORE_NAME = "sismik_settings"
}