package io.github.patxibocos.mycyclist.ui.teams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private val teamId: String = savedStateHandle["teamId"]!!

    val teamViewState: StateFlow<TeamViewState> =
        combine(dataRepository.teams, dataRepository.riders) { teams, riders ->
            val team = teams.find { it.id == teamId }!!
            val teamRiders = teamRiders(defaultDispatcher, team, riders)
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

private suspend fun teamRiders(
    defaultDispatcher: CoroutineDispatcher,
    team: Team,
    riders: List<Rider>,
): List<Rider> {
    return withContext(defaultDispatcher) {
        riders.filter { team.riderIds.contains(it.id) }
    }
}
