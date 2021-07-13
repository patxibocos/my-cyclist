package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.json.DataProvider
import io.github.patxibocos.roadcyclingdata.data.json.Team
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(private val dataProvider: DataProvider) : ViewModel() {

    fun getTeams(): Flow<List<Team>> = dataProvider.teams()

}