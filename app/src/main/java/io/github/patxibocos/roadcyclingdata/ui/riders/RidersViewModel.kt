package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.TeamsAndRidersRepository
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
    private val _selectedRider: MutableStateFlow<Rider?> = MutableStateFlow(null)
    private val _selectedRiderIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    val riders: StateFlow<List<Rider>> = _riders
    val selectedRiderIndex: StateFlow<Int> = _selectedRiderIndex

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
            val ridersFlow = teamsAndRidersRepository.riders()
            combine(ridersFlow, _search, _selectedRider) { riders, query, selectedRider ->
                val filteredRiders = riders.filter(query)
                val selectedRiderIndex = filteredRiders.indexOf(selectedRider)
                filteredRiders to selectedRiderIndex
            }.collect { (filteredRiders, selectedRiderIndex) ->
                _riders.emit(filteredRiders)
                _selectedRiderIndex.emit(selectedRiderIndex)
            }
        }
    }

    fun onSearched(query: String) {
        viewModelScope.launch {
            _search.emit(query)
        }
    }

    fun onRiderSelected(rider: Rider) {
        viewModelScope.launch {
            if (_selectedRider.value == rider) {
                _selectedRider.emit(null)
            } else {
                _selectedRider.emit(rider)
            }
        }
    }
}
