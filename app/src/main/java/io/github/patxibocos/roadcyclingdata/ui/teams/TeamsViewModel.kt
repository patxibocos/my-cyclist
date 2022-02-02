package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass.Team
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    val teams: Flow<List<Team>> = dataRepository.teams
}
