package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _search = MutableStateFlow("")

    val state: Flow<UiState> = combine(dataRepository.riders, _search) { riders, query ->
        val filteredRiders = riders.filter(query)
        UiState(filteredRiders, query)
    }

    fun onSearched(query: String) {
        _search.value = query
    }
}

private fun List<Rider>.filter(query: String): List<Rider> {
    val querySplits = query.trim().split(" ").map { it.trim() }
    return this.filter { rider ->
        // For each of the split, it should be contained either on first or last name
        querySplits.all { q ->
            rider.firstName.contains(
                q,
                ignoreCase = true
            ) || rider.lastName.contains(q, ignoreCase = true)
        }
    }
}

data class UiState(val riders: List<Rider>, val search: String) {
    companion object {
        val Empty = UiState(emptyList(), "")
    }
}
