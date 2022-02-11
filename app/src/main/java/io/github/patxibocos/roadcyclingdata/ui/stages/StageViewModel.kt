package io.github.patxibocos.roadcyclingdata.ui.stages

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Stage
import io.github.patxibocos.roadcyclingdata.data.DataRepository
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
    ) { (raceId, stageId), races -> races.find { it.id == raceId }?.stagesList?.find { it.id == stageId } }

    fun loadStage(raceId: String, stageId: String) {
        _raceAndStageId.value = raceId to stageId
    }
}
