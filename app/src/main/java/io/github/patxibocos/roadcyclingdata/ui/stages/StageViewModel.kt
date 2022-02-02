package io.github.patxibocos.roadcyclingdata.ui.stages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Stage
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StageViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _raceAndStageId = MutableSharedFlow<Pair<String, String>>()

    val stage: Flow<Stage?> = combine(
        _raceAndStageId,
        dataRepository.races
    ) { (raceId, stageId), races -> races.find { it.id == raceId }?.stagesList?.find { it.id == stageId } }

    fun loadStage(raceId: String, stageId: String) {
        viewModelScope.launch {
            _raceAndStageId.emit(raceId to stageId)
        }
    }
}
