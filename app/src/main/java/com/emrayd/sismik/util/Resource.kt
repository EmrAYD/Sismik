package com.emrayd.sismik.util

/**
 * Repository'den ViewModel'e veri akışını saran genel amaçlı sarmalayıcı.
 * Bir işlemin üç durumdan birinde olduğunu temsil eder:
 *  - Loading: veri yükleniyor (ilk yükleme veya yenileme)
 *  - Success: veri başarıyla geldi
 *  - Error: bir hata oluştu, mesaj ve (varsa) eski/cache'lenmiş veri taşınabilir
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val message: String, val data: T? = null) : Resource<T>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()
}