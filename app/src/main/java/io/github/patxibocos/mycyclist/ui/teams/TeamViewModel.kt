package io.github.patxibocos.mycyclist.ui.teams

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val teamId: String = savedStateHandle["teamId"]!!

    val teamViewState: Flow<TeamViewState> =
        combine(dataRepository.teams, dataRepository.riders) { teams, riders ->
            val team = teams.find { it.id == teamId }
            val teamRiderIds = team?.riderIds ?: emptyList()
            val teamRiders = riders.filter { teamRiderIds.contains(it.id) }
            TeamViewState(team, teamRiders)
        }
}

@Immutable
@Stable
data class TeamViewState(
    val team: Team? = null,
    val riders: List<Rider> = emptyList(),
) {
    companion object {
        val Empty = TeamViewState()
    }
}
