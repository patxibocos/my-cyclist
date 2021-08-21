package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    fun getTeam(teamId: String): Flow<Team> =
        dataRepository.teams().mapNotNull { teams -> teams.find { it.id == teamId } }
}
