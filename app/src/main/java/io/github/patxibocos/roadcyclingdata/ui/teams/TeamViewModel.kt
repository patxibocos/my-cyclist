package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.ui.data.TeamOfRiders
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTeamOfRiders(teamId: String): Flow<TeamOfRiders> =
        dataRepository.teams().mapNotNull { teams -> teams.find { it.id == teamId } }
            .flatMapLatest { team ->
                dataRepository.riders()
                    .map { riders -> riders.filter { team.riderIdsList.contains(it.id) } }
                    .map { teamRiders -> TeamOfRiders(team, teamRiders) }
            }
}
