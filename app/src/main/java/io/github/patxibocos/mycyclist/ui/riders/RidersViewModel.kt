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
    private val _sorting = MutableStateFlow(Sorting.LastName)
    private val _searching = MutableStateFlow(false)

    val state: Flow<UiState> =
        combine(
            dataRepository.riders,
            dataRepository.teams,
            _searching,
            _search,
            _sorting
        ) { riders, teams, searching, query, sorting ->
            val filteredRiders = searchRiders(defaultDispatcher, riders, query)
            when (sorting) {
                Sorting.LastName -> UiState(
                    UiState.UiRiders.RidersByLastName(
                        filteredRiders.groupBy {
                            it.lastName.first().uppercaseChar()
                        }
                    ),
                    searching,
                    query
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
                    UiState(UiState.UiRiders.RidersByTeam(ridersByTeam), searching, query)
                }
                Sorting.Country -> UiState(
                    UiState.UiRiders.RidersByCountry(
                        filteredRiders.groupBy { it.country }
                            .toSortedMap()
                    ),
                    searching,
                    query
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

data class UiState(val riders: UiRiders, val searching: Boolean, val search: String) {

    sealed class UiRiders(val sorting: Sorting) {
        data class RidersByLastName(val riders: Map<Char, List<Rider>>) : UiRiders(Sorting.LastName)
        data class RidersByTeam(val riders: Map<Team, List<Rider>>) : UiRiders(Sorting.Team)
        data class RidersByCountry(val riders: Map<String, List<Rider>>) : UiRiders(Sorting.Country)
    }

    companion object {
        val Empty = UiState(UiRiders.RidersByLastName(emptyMap()), false, "")
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
