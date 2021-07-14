package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.json.Rider
import io.github.patxibocos.roadcyclingdata.data.json.TeamsAndRidersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(teamsAndRidersRepository: TeamsAndRidersRepository) :
    ViewModel() {

    private val _riders: MutableStateFlow<List<Rider>> = MutableStateFlow(emptyList())
    private val _search = MutableStateFlow("")
    val riders: StateFlow<List<Rider>> = _riders

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

    init {
        viewModelScope.launch(Dispatchers.Default) {
            teamsAndRidersRepository.riders().combine(_search) { riders, query ->
                riders.filter(query)
            }.collect { filteredRiders ->
                _riders.emit(filteredRiders)
            }
        }
    }

    fun onSearched(query: String) {
        viewModelScope.launch {
            _search.emit(query)
        }
    }

}