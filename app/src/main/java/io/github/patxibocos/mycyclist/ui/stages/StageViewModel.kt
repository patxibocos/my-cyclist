package io.github.patxibocos.mycyclist.ui.stages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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

    val stageViewState: Flow<StageViewState> =
        combine(dataRepository.races, dataRepository.riders) { races, riders ->
            val stage = races.find { it.id == raceId }!!.stages.find { it.id == stageId }!!
            val riderResults = stage.result.map { riderResult ->
                RiderResult(
                    riders.find { it.id == riderResult.riderId }!!,
                    riderResult.time
                )
            }
            StageViewState(stage, riderResults)
        }
}

@Immutable
data class StageViewState(
    val stage: Stage? = null,
    val ridersResult: List<RiderResult> = emptyList()
) {
    companion object {
        val Empty = StageViewState()
    }
}

@Immutable
data class RiderResult(val rider: Rider, val time: Long)
