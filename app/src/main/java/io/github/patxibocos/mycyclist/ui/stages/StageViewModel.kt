package io.github.patxibocos.mycyclist.ui.stages

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Stage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class StageViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _raceAndStageId = MutableStateFlow("" to "")

    val stage: Flow<Stage?> = combine(
        _raceAndStageId,
        dataRepository.races
    ) { (raceId, stageId), races -> races.find { it.id == raceId }?.stages?.find { it.id == stageId } }

    fun loadStage(raceId: String, stageId: String) {
        _raceAndStageId.value = raceId to stageId
    }
}
