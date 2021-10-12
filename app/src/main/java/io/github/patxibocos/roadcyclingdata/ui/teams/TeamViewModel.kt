package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.ui.data.TeamOfRiders
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _teamId = MutableLiveData<String>()

    val teamOfRiders = _teamId.switchMap { teamId ->
        liveData {
            combine(dataRepository.teams(), dataRepository.riders()) { teams, riders ->
                teams to riders
            }.collect { (teams, riders) ->
                teams.find { it.id == teamId }?.let { team ->
                    val teamRiders = riders.filter { team.riderIdsList.contains(it.id) }
                    val teamOfRiders = TeamOfRiders(team, teamRiders)
                    emit(teamOfRiders)
                }
            }
        }
    }

    fun loadTeam(teamId: String) {
        _teamId.value = teamId
    }
}
