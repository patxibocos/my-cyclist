package io.github.patxibocos.mycyclist.ui.stages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class StageViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!
    private val stageId: String = savedStateHandle["stageId"]!!

    val stageViewState: StateFlow<StageViewState> =
        combine(dataRepository.races, dataRepository.riders) { races, riders ->
            val race = races.find { it.id == raceId }!!
            val stage = race.stages.find { it.id == stageId }!!
            val riderResults = stage.result.map { riderResult ->
                RiderResult(
                    riders.find { it.id == riderResult.riderId }!!,
                    riderResult.time
                )
            }
            StageViewState(race, stage, race.stages.indexOf(stage) + 1, riderResults)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = StageViewState.Empty
        )
}

@Immutable
data class StageViewState(
    val race: Race?,
    val stage: Stage?,
    val stageNumber: Int,
    val ridersResult: List<RiderResult>
) {
    companion object {
        val Empty =
            StageViewState(race = null, stage = null, stageNumber = 0, ridersResult = emptyList())
    }
}

@Immutable
data class RiderResult(val rider: Rider, val time: Long)
