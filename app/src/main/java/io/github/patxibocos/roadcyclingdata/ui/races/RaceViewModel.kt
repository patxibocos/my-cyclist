package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Race
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _raceId = MutableStateFlow("")

    val race: Flow<Race?> =
        combine(
            _raceId,
            dataRepository.races
        ) { raceId, races -> races.find { it.id == raceId } }

    fun loadRace(raceId: String) {
        _raceId.value = raceId
    }
}
