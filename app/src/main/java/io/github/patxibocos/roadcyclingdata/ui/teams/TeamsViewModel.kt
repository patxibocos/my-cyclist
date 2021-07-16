package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamsWithRidersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(private val teamsWithRidersRepository: TeamsWithRidersRepository) :
    ViewModel() {

    fun getTeams(): Flow<List<Team>> = teamsWithRidersRepository.teamsWithRiders().map { it.teams }

}