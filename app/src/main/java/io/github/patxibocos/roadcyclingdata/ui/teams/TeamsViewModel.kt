package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.db.Team
import io.github.patxibocos.roadcyclingdata.data.db.TeamDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(private val teamDao: TeamDao) : ViewModel() {

    fun getTeams(): Flow<List<Team>> {
        return teamDao.getTeams()
    }

}