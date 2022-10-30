package io.github.patxibocos.mycyclist.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Rider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class RidersViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher
) :
    ViewModel() {

    private val _search = MutableStateFlow("")
    private val _searching = MutableStateFlow(false)
    private val _sorting = MutableStateFlow(Sorting.UciRanking)
    private val _refreshing = MutableStateFlow(false)

    val topBarState: StateFlow<TopBarState> =
        combine(_search, _searching, _sorting) { search, searching, sorting ->
            TopBarState(search, searching, sorting)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TopBarState.Empty
        )

    val ridersState: StateFlow<RidersViewState> =
        combine(
            dataRepository.riders,
            _search,
            _sorting,
            _refreshing
        ) { riders, query, sorting, refreshing ->
            val filteredRiders = searchRiders(defaultDispatcher, riders, query)
            val groupedRiders = when (sorting) {
                Sorting.LastName -> RidersViewState.Riders.ByLastName(
                    filteredRiders.groupBy {
                        it.lastName.first().uppercaseChar()
                    }
                )

                Sorting.Country -> RidersViewState.Riders.ByCountry(
                    filteredRiders.groupBy { it.country }
                        .toSortedMap()
                )

                Sorting.UciRanking -> RidersViewState.Riders.ByUciRanking(filteredRiders.sortedBy { if (it.uciRankingPosition > 0) it.uciRankingPosition else Int.MAX_VALUE })
            }
            RidersViewState(riders = groupedRiders, isRefreshing = refreshing)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RidersViewState.Empty
        )

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

    fun onRefreshed() {
        viewModelScope.launch {
            _refreshing.value = true
            delay(500 - measureTimeMillis { dataRepository.refresh() })
            _refreshing.value = false
        }
    }
}

enum class Sorting {
    LastName,
    Country,
    UciRanking
}

@Immutable
data class RidersViewState(val riders: Riders, val isRefreshing: Boolean) {

    @Immutable
    sealed class Riders {
        @Immutable
        data class ByLastName(val riders: Map<Char, List<Rider>>) : Riders()

        @Immutable
        data class ByCountry(val riders: Map<String, List<Rider>>) : Riders()

        @Immutable
        data class ByUciRanking(val riders: List<Rider>) : Riders()
    }

    companion object {
        val Empty = RidersViewState(Riders.ByUciRanking(emptyList()), false)
    }
}

@Immutable
data class TopBarState(
    val search: String,
    val searching: Boolean,
    val sorting: Sorting
) {
    companion object {
        val Empty = TopBarState(search = "", searching = false, sorting = Sorting.UciRanking)
    }
}

suspend fun searchRiders(
    defaultDispatcher: CoroutineDispatcher,
    riders: List<Rider>,
    query: String
): List<Rider> = withContext(defaultDispatcher) {
    if (query.isBlank()) {
        return@withContext riders
    }
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
