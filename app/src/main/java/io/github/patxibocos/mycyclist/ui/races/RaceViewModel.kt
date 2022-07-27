package io.github.patxibocos.mycyclist.ui.races

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!

    val raceViewState: StateFlow<RaceViewState> =
        dataRepository.races.map { races ->
            val race = races.find { it.id == raceId }
            RaceViewState(race)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RaceViewState.Empty
        )
}

@Immutable
data class RaceViewState(val race: Race?) {
    companion object {
        val Empty = RaceViewState(race = null)
    }
}
