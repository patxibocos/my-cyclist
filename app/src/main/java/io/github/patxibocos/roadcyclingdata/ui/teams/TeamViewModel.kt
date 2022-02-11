package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.ui.data.TeamOfRiders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _teamId = MutableStateFlow("")

    val teamOfRiders: Flow<TeamOfRiders?> =
        combine(_teamId, dataRepository.teams, dataRepository.riders) { teamId, teams, riders ->
            teams.find { it.id == teamId }?.let { team ->
                val teamRiders = riders.filter { team.riderIdsList.contains(it.id) }
                TeamOfRiders(team, teamRiders)
            }
        }

    fun loadTeam(teamId: String) {
        _teamId.value = teamId
    }
}
