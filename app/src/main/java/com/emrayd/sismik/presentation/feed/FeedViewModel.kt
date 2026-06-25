package com.emrayd.sismik.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrayd.sismik.domain.model.Earthquake
import com.emrayd.sismik.domain.usecase.FilterEarthquakesUseCase
import com.emrayd.sismik.domain.usecase.GetLiveEarthquakesUseCase
import com.emrayd.sismik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
data class FeedUiState(
    val earthquakes: List<Earthquake> = emptyList(),
    val sortType: FilterEarthquakesUseCase.SortType = FilterEarthquakesUseCase.SortType.DATE_DESC,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    getLiveEarthquakesUseCase: GetLiveEarthquakesUseCase,
    private val filterEarthquakesUseCase: FilterEarthquakesUseCase
) : ViewModel() {

    private val sortType = MutableStateFlow(FilterEarthquakesUseCase.SortType.DATE_DESC)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<FeedUiState> = combine(
        getLiveEarthquakesUseCase(),
        sortType,
        searchQuery
    ) { resource, currentSortType, currentQuery ->
        val allEarthquakes = when (resource) {
            is Resource.Success -> resource.data
            is Resource.Loading -> resource.data ?: emptyList()
            is Resource.Error -> resource.data ?: emptyList()
        }

        val filteredAndSorted = filterEarthquakesUseCase(
            earthquakes = allEarthquakes,
            sortType = currentSortType,
            cityQuery = currentQuery
        )

        FeedUiState(
            earthquakes = filteredAndSorted,
            sortType = currentSortType,
            searchQuery = currentQuery,
            isLoading = resource is Resource.Loading,
            errorMessage = (resource as? Resource.Error)?.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FeedUiState(isLoading = true)
    )

    fun onSortTypeSelected(type: FilterEarthquakesUseCase.SortType) {
        sortType.value = type
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
}