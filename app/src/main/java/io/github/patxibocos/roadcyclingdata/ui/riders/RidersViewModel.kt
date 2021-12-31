package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _search = MutableStateFlow("")

    val riders = combine(dataRepository.riders(), _search) { riders, query ->
        riders.filter(query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

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

    fun onSearched(query: String) {
        viewModelScope.launch {
            _search.emit(query)
        }
    }
}
