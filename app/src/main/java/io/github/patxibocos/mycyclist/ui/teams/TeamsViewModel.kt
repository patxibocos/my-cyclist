package io.github.patxibocos.mycyclist.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Team
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    val teamsViewState: StateFlow<TeamsViewState> =
        dataRepository.teams.map(::TeamsViewState).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TeamsViewState.Empty
        )
}

@Immutable
data class TeamsViewState(val teams: List<Team>) {
    companion object {
        val Empty = TeamsViewState(teams = emptyList())
    }
}
