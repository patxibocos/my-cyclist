package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _races: MutableStateFlow<List<Race>> = MutableStateFlow(emptyList())
    private val _selectedRace: MutableStateFlow<Race?> = MutableStateFlow(null)
    private val _selectedRaceIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    val races: StateFlow<List<Race>> = _races
    val selectedRiderIndex: StateFlow<Int> = _selectedRaceIndex

    init {
        viewModelScope.launch(Dispatchers.Default) {
            val racesFlow = dataRepository.races()
            combine(racesFlow, _selectedRace) { races, selectedRider ->
                val selectedRaceIndex = races.indexOf(selectedRider)
                races to selectedRaceIndex
            }.collect { (races, selectedRaceIndex) ->
                _races.emit(races)
                _selectedRaceIndex.emit(selectedRaceIndex)
            }
        }
    }

    fun onRaceSelected(race: Race) {
        viewModelScope.launch {
            if (_selectedRace.value == race) {
                _selectedRace.emit(null)
            } else {
                _selectedRace.emit(race)
            }
        }
    }
}
