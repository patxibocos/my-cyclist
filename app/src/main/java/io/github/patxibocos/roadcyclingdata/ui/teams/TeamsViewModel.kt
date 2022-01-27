package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass.Team
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    val teams: StateFlow<List<Team>> =
        dataRepository.teams.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )
}
