package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Race
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _raceId = MutableSharedFlow<String>()

    val race: StateFlow<Race?> = combine(_raceId, dataRepository.races()) { raceId, races ->
        races.find { it.id == raceId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null,
    )

    fun loadRace(raceId: String) {
        viewModelScope.launch {
            _raceId.emit(raceId)
        }
    }
}
