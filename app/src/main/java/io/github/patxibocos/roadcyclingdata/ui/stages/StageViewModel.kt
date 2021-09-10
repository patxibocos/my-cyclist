package io.github.patxibocos.roadcyclingdata.ui.stages

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Stage
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class StageViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    fun getStage(raceId: String, stageId: String): Flow<Stage> =
        dataRepository.races().mapNotNull { races -> races.find { it.id == raceId } }
            .mapNotNull { it.stagesList.find { stage -> stage.id == stageId } }
}
