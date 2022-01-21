package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Race
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    val races: StateFlow<List<Race>> =
        dataRepository.races().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )
}
