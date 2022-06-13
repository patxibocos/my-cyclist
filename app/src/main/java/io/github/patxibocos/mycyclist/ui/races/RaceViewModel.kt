package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!

    val raceViewState: Flow<RaceViewState> =
        dataRepository.races.map { races ->
            val race = races.find { it.id == raceId }
            RaceViewState(race)
        }
}

@Immutable
@Stable
data class RaceViewState(
    val race: Race? = null,
) {
    companion object {
        val Empty = RaceViewState()
    }
}
