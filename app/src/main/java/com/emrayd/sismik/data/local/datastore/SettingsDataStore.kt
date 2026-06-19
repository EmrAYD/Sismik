package com.emrayd.sismik.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emrayd.sismik.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.SETTINGS_DATASTORE_NAME
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val USER_CITY = stringPreferencesKey("user_city")
        val MIN_MAGNITUDE = floatPreferencesKey("min_magnitude_threshold")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val LAST_NOTIFIED_ID = stringPreferencesKey("last_notified_earthquake_id")
    }

    val userCityFlow: Flow<String> = context.dataStore.data.map { it[Keys.USER_CITY] ?: "" }
    suspend fun getUserCity(): String = context.dataStore.data.first()[Keys.USER_CITY] ?: ""
    suspend fun setUserCity(city: String) { context.dataStore.edit { it[Keys.USER_CITY] = city } }

    val minMagnitudeFlow: Flow<Float> = context.dataStore.data.map { it[Keys.MIN_MAGNITUDE] ?: 4.0f }
    suspend fun getMinMagnitude(): Float = context.dataStore.data.first()[Keys.MIN_MAGNITUDE] ?: 4.0f
    suspend fun setMinMagnitude(magnitude: Float) { context.dataStore.edit { it[Keys.MIN_MAGNITUDE] = magnitude } }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }
    suspend fun areNotificationsEnabled(): Boolean = context.dataStore.data.first()[Keys.NOTIFICATIONS_ENABLED] ?: true
    suspend fun setNotificationsEnabled(enabled: Boolean) { context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled } }

    suspend fun getLastNotifiedId(): String = context.dataStore.data.first()[Keys.LAST_NOTIFIED_ID] ?: ""
    suspend fun setLastNotifiedId(id: String) { context.dataStore.edit { it[Keys.LAST_NOTIFIED_ID] = id } }
}