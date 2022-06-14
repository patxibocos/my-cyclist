package io.github.patxibocos.mycyclist.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(
    dataRepository: DataRepository,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private val _search = MutableStateFlow("")
    private val _searching = MutableStateFlow(false)
    private val _sorting = MutableStateFlow(Sorting.LastName)

    val topBarState: Flow<TopBarState> =
        combine(_search, _searching, _sorting) { search, searching, sorting ->
            TopBarState(search, searching, sorting)
        }

    val ridersState: Flow<RidersViewState> =
        combine(
            dataRepository.riders,
            dataRepository.teams,
            _search,
            _sorting
        ) { riders, teams, query, sorting ->
            val filteredRiders = searchRiders(defaultDispatcher, riders, query)
            when (sorting) {
                Sorting.LastName -> RidersViewState(
                    RidersViewState.Riders.ByLastName(
                        filteredRiders.groupBy {
                            it.lastName.first().uppercaseChar()
                        }
                    ),
                )
                Sorting.Team -> {
                    val ridersByTeam =
                        teams.associateWith { team ->
                            filteredRiders.filter {
                                team.riderIds.contains(
                                    it.id
                                )
                            }
                        }.filter { it.value.isNotEmpty() }
                    RidersViewState(RidersViewState.Riders.ByTeam(ridersByTeam))
                }
                Sorting.Country -> RidersViewState(
                    RidersViewState.Riders.ByCountry(
                        filteredRiders.groupBy { it.country }
                            .toSortedMap()
                    )
                )
            }
        }

    fun onSearched(query: String) {
        _search.value = query
    }

    fun onSorted(sorting: Sorting) {
        _sorting.value = sorting
    }

    fun onToggled() {
        _searching.value = !_searching.value
        if (!_searching.value) {
            _search.value = ""
        }
    }
}

enum class Sorting {
    LastName,
    Team,
    Country
}

data class RidersViewState(val riders: Riders) {

    sealed class Riders {
        data class ByLastName(val riders: Map<Char, List<Rider>>) : Riders()
        data class ByTeam(val riders: Map<Team, List<Rider>>) : Riders()
        data class ByCountry(val riders: Map<String, List<Rider>>) : Riders()
    }

    companion object {
        val Empty = RidersViewState(Riders.ByLastName(emptyMap()))
    }
}

data class TopBarState(
    val search: String = "",
    val searching: Boolean = false,
    val sorting: Sorting = Sorting.LastName
) {
    companion object {
        val Empty = TopBarState()
    }
}

suspend fun searchRiders(
    defaultDispatcher: CoroutineDispatcher,
    riders: List<Rider>,
    query: String,
): List<Rider> = withContext(defaultDispatcher) {
    val querySplits = query.trim().split(" ").map { it.trim() }
    riders.filter { rider ->
        // For each of the split, it should be contained either on first or last name
        querySplits.all { q ->
            rider.firstName.contains(
                q,
                ignoreCase = true
            ) || rider.lastName.contains(q, ignoreCase = true)
        }
    }
}
