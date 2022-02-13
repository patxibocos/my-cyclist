package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    val races: Flow<List<Race>> = dataRepository.races
}
