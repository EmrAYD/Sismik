package com.emrayd.sismik.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrayd.sismik.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userCity: String = "",
    val minMagnitude: Float = 4.0f,
    val notificationsEnabled: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsDataStore.userCityFlow,
        settingsDataStore.minMagnitudeFlow,
        settingsDataStore.notificationsEnabledFlow
    ) { city, magnitude, notificationsEnabled ->
        SettingsUiState(
            userCity = city,
            minMagnitude = magnitude,
            notificationsEnabled = notificationsEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun saveCity(city: String) {
        viewModelScope.launch { settingsDataStore.setUserCity(city.trim()) }
    }

    fun saveMinMagnitude(magnitude: Float) {
        viewModelScope.launch { settingsDataStore.setMinMagnitude(magnitude) }
    }

    fun saveNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setNotificationsEnabled(enabled) }
    }
}