package io.github.patxibocos.mycyclist.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.ui.data.TeamDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _teamId = MutableStateFlow("")

    val teamDetails: Flow<TeamDetails?> =
        combine(_teamId, dataRepository.teams, dataRepository.riders) { teamId, teams, riders ->
            teams.find { it.id == teamId }?.let { team ->
                val teamRiders = riders.filter { team.riderIds.contains(it.id) }
                TeamDetails(team, teamRiders)
            }
        }

    fun loadTeam(teamId: String) {
        _teamId.value = teamId
    }
}
