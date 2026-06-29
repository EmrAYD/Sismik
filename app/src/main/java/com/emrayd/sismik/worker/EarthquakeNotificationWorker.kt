package com.emrayd.sismik.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emrayd.sismik.data.local.datastore.SettingsDataStore
import com.emrayd.sismik.domain.repository.EarthquakeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
@HiltWorker
class EarthquakeNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: EarthquakeRepository,
    private val settingsDataStore: SettingsDataStore
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (!settingsDataStore.areNotificationsEnabled()) return Result.success()

            val earthquakes = repository.refreshEarthquakes()
            if (earthquakes.isEmpty()) return Result.success()

            val minMagnitude = settingsDataStore.getMinMagnitude().toDouble()
            val lastNotifiedId = settingsDataStore.getLastNotifiedId()
            val userCity = settingsDataStore.getUserCity()

            val newEarthquake = earthquakes
                .sortedByDescending { it.epochSeconds }
                .firstOrNull { earthquake ->
                    // Büyüklük eşiğini geç
                    earthquake.magnitude >= minMagnitude &&
                            // Daha önce bildirilmemiş olsun
                            earthquake.id != lastNotifiedId &&
                            // Şehir seçilmemişse tüm depremleri bildir,
                            // seçilmişse sadece o şehri etkileyen depremleri bildir
                            (userCity.isBlank() ||
                                    earthquake.epicenterCity.equals(userCity, ignoreCase = true) ||
                                    earthquake.closestCities.any { it.equals(userCity, ignoreCase = true) } ||
                                    earthquake.closestCity.equals(userCity, ignoreCase = true))
                }

            if (newEarthquake != null) {
                NotificationHelper.showEarthquakeNotification(
                    context = applicationContext,
                    earthquakeId = newEarthquake.id,
                    title = newEarthquake.title,
                    magnitude = newEarthquake.magnitude
                )
                settingsDataStore.setLastNotifiedId(newEarthquake.id)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}