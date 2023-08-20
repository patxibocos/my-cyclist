package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.riders.searchRiders
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RaceParticipationsViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!

    private val _search = MutableStateFlow("")
    private val _searching = MutableStateFlow(false)

    val topBarState: StateFlow<TopBarState> =
        combine(_search, _searching) { search, searching ->
            TopBarState(search, searching)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TopBarState.Empty,
        )

    val raceParticipationsViewState: StateFlow<RaceParticipationsViewState> =
        combine(
            _search,
            dataRepository.races,
            dataRepository.riders,
            dataRepository.teams,
        ) { query, races, riders, teams ->
            val race = races.find { it.id == raceId }!!
            val filteredRiders = searchParticipants(
                defaultDispatcher,
                riders,
                race.teamParticipations.flatMap { it.riderParticipations },
                query,
            )
            val riderParticipationsByTeam =
                race.teamParticipations.mapNotNull { teamParticipation ->
                    val team = teams.find { it.id == teamParticipation.teamId }!!
                    val teamRiders =
                        teamParticipation.riderParticipations.mapNotNull { riderParticipation ->
                            val rider = filteredRiders.find { it.id == riderParticipation.riderId }
                                ?: return@mapNotNull null
                            RiderParticipation(rider, riderParticipation.number)
                        }
                    if (teamRiders.isEmpty()) {
                        return@mapNotNull null
                    }
                    team to teamRiders
                }
            RaceParticipationsViewState(riderParticipationsByTeam.toMap())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RaceParticipationsViewState.Empty,
        )

    fun onSearched(query: String) {
        _search.value = query
    }

    fun onToggled() {
        _searching.value = !_searching.value
        if (!_searching.value) {
            _search.value = ""
        }
    }
}

@Immutable
data class RiderParticipation(val rider: Rider, val number: Int)

@Immutable
data class RaceParticipationsViewState(val ridersByTeam: Map<Team, List<RiderParticipation>>) {
    companion object {
        val Empty = RaceParticipationsViewState(emptyMap())
    }
}

@Immutable
data class TopBarState(
    val search: String,
    val searching: Boolean,
) {
    companion object {
        val Empty = TopBarState(search = "", searching = false)
    }
}

private suspend fun searchParticipants(
    defaultDispatcher: CoroutineDispatcher,
    riders: List<Rider>,
    riderParticipations: List<io.github.patxibocos.mycyclist.data.RiderParticipation>,
    query: String,
): List<Rider> = withContext(defaultDispatcher) {
    query.trim().toIntOrNull()?.let {
        riderParticipations.filter { (_, number) -> number.toString().contains(it.toString()) }
            .mapNotNull { (riderId, _) -> riders.find { it.id == riderId } }
    } ?: searchRiders(defaultDispatcher, riders, query)
}
