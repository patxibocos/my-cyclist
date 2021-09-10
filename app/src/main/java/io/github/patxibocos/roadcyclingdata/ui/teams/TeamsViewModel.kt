package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _teams: MutableStateFlow<List<Team>> = MutableStateFlow(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    init {
        viewModelScope.launch(Dispatchers.Default) {
            dataRepository.teams().collect { teams ->
                _teams.emit(teams)
            }
        }
    }
}
