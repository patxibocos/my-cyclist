package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _search = MutableStateFlow("")
    private val _sorting = MutableStateFlow(Sorting.LastName)

    val state: Flow<UiState> =
        combine(
            dataRepository.riders,
            dataRepository.teams,
            _search,
            _sorting
        ) { riders, teams, query, sorting ->
            val filteredRiders = riders.filter(query)
            when (sorting) {
                Sorting.LastName -> UiState(
                    UiState.UiRiders.RidersByLastName(filteredRiders.groupBy { it.lastName.first() }),
                    query
                )
                Sorting.Team -> {
                    val ridersByTeam =
                        teams.associateWith { team -> riders.filter { team.riderIds.contains(it.id) } }
                    UiState(UiState.UiRiders.RidersByTeam(ridersByTeam), query)
                }
                Sorting.Country -> UiState(
                    UiState.UiRiders.RidersByCountry(
                        filteredRiders.groupBy { it.country }
                            .toSortedMap()
                    ),
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
}

private fun List<Rider>.filter(query: String): List<Rider> {
    val querySplits = query.trim().split(" ").map { it.trim() }
    return this.filter { rider ->
        // For each of the split, it should be contained either on first or last name
        querySplits.all { q ->
            rider.firstName.contains(
                q,
                ignoreCase = true
            ) || rider.lastName.contains(q, ignoreCase = true)
        }
    }
}

enum class Sorting {
    LastName,
    Team,
    Country
}

data class UiState(val riders: UiRiders, val search: String) {

    sealed class UiRiders(val sorting: Sorting) {
        data class RidersByLastName(val riders: Map<Char, List<Rider>>) : UiRiders(Sorting.LastName)
        data class RidersByTeam(val riders: Map<Team, List<Rider>>) : UiRiders(Sorting.Team)
        data class RidersByCountry(val riders: Map<String, List<Rider>>) : UiRiders(Sorting.Country)
    }

    companion object {
        val Empty = UiState(UiRiders.RidersByLastName(emptyMap()), "")
    }
}
