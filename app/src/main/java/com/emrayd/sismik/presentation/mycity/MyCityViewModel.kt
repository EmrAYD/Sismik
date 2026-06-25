package com.emrayd.sismik.presentation.mycity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrayd.sismik.data.local.datastore.SettingsDataStore
import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.domain.usecase.GetLiveEarthquakesUseCase
import com.emrayd.sismik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
data class MyCityUiState(
    val userCity: String = "",
    val earthquakes: List<Earthquake> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isCityNotSet: Boolean get() = userCity.isBlank()
}

@HiltViewModel
class MyCityViewModel @Inject constructor(
    getLiveEarthquakesUseCase: GetLiveEarthquakesUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<MyCityUiState> = combine(
        settingsDataStore.userCityFlow,
        getLiveEarthquakesUseCase()
    ) { city, resource ->
        val allEarthquakes = when (resource) {
            is Resource.Success -> resource.data
            is Resource.Loading -> resource.data ?: emptyList()
            is Resource.Error -> resource.data ?: emptyList()
        }

        val filtered = if (city.isBlank()) {
            emptyList()
        } else {
            allEarthquakes.filter { earthquake ->
                earthquake.closestCities.any { it.equals(city, ignoreCase = true) } ||
                        earthquake.closestCity.equals(city, ignoreCase = true)
            }
        }

        MyCityUiState(
            userCity = city,
            earthquakes = filtered,
            isLoading = resource is Resource.Loading,
            errorMessage = (resource as? Resource.Error)?.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyCityUiState(isLoading = true)
    )
}