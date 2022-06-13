package io.github.patxibocos.mycyclist.ui.stages

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Stage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class StageViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!
    private val stageId: String = savedStateHandle["stageId"]!!

    val stateViewState: Flow<StageViewState> = dataRepository.races.map { races ->
        val stage = races.find { it.id == raceId }?.stages?.find { it.id == stageId }
        StageViewState(stage)
    }
}

@Immutable
@Stable
data class StageViewState(
    val stage: Stage? = null,
) {
    companion object {
        val Empty = StageViewState()
    }
}
