package io.github.patxibocos.mycyclist.ui.teams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    private val teamId: String = savedStateHandle["teamId"]!!

    val teamViewState: StateFlow<TeamViewState> =
        combine(dataRepository.teams, dataRepository.riders) { teams, riders ->
            val team = teams.find { it.id == teamId }
            val teamRiderIds = team?.riderIds ?: emptyList()
            val teamRiders = riders.filter { teamRiderIds.contains(it.id) }
            TeamViewState(team, teamRiders)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TeamViewState.Empty,
        )
}

@Immutable
data class TeamViewState(
    val team: Team?,
    val riders: List<Rider>,
) {
    companion object {
        val Empty = TeamViewState(team = null, riders = emptyList())
    }
}
