package io.github.patxibocos.mycyclist.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Team
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class TeamsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _refreshing = MutableStateFlow(false)

    val teamsViewState: StateFlow<TeamsViewState> =
        combine(dataRepository.teams, _refreshing) { teams, refreshing ->
            TeamsViewState(teams = teams, isRefreshing = refreshing)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TeamsViewState.Empty,
        )

    fun onRefreshed() {
        viewModelScope.launch {
            _refreshing.value = true
            delay(500 - measureTimeMillis { dataRepository.refresh() })
            _refreshing.value = false
        }
    }
}

@Immutable
data class TeamsViewState(val teams: List<Team>, val isRefreshing: Boolean) {
    companion object {
        val Empty = TeamsViewState(teams = emptyList(), false)
    }
}
