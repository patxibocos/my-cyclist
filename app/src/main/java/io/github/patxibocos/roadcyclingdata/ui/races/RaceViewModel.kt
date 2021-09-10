package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Race
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    fun getRace(raceId: String): Flow<Race> =
        dataRepository.races().mapNotNull { races -> races.find { it.id == raceId } }
}
