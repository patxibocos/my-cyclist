package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamsAndRidersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(private val teamsAndRidersRepository: TeamsAndRidersRepository) :
    ViewModel() {

    fun getTeams(): Flow<List<Team>> = teamsAndRidersRepository.teams()

}