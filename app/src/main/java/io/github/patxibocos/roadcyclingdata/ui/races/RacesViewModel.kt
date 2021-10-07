package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Race
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    fun races(): Flow<List<Race>> = dataRepository.races()
}
