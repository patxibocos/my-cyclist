package io.github.patxibocos.mycyclist.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.isActive
import io.github.patxibocos.mycyclist.data.isFuture
import io.github.patxibocos.mycyclist.data.isPast
import io.github.patxibocos.mycyclist.data.isSingleDay
import io.github.patxibocos.mycyclist.data.todayStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class RacesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _refreshing = MutableStateFlow(false)

    val racesViewState: StateFlow<RacesViewState> =
        combine(dataRepository.races, _refreshing) { races, refreshing ->
            val minStartDate = races.first().startDate
            val maxEndDate = races.last().endDate
            val today = LocalDate.now(ZoneId.systemDefault())
            when {
                today.isBefore(minStartDate) -> RacesViewState.SeasonNotStartedViewState(
                    races,
                    refreshing
                )

                today.isAfter(maxEndDate) -> RacesViewState.SeasonEndedViewState(
                    races,
                    refreshing
                )

                else -> {
                    val todayStages = races.filter(Race::isActive).map { race ->
                        val todayStage = race.todayStage()
                        when {
                            race.isSingleDay() -> TodayStage.SingleDayRace(
                                race,
                                race.stages.first()
                            )

                            todayStage != null -> TodayStage.MultiStageRace(
                                race,
                                todayStage.first,
                                todayStage.second + 1
                            )

                            else -> TodayStage.RestDay(race)
                        }
                    }
                    RacesViewState.SeasonInProgressViewState(
                        todayStages = todayStages,
                        pastRaces = races.filter(Race::isPast).reversed(),
                        futureRaces = races.filter(Race::isFuture),
                        isRefreshing = refreshing
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RacesViewState.Empty
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
sealed class TodayStage(open val race: Race) {
    data class RestDay(override val race: Race) : TodayStage(race)
    data class SingleDayRace(override val race: Race, val stage: Stage) : TodayStage(race)
    data class MultiStageRace(override val race: Race, val stage: Stage, val stageNumber: Int) :
        TodayStage(race)
}

@Immutable
sealed class RacesViewState(open val isRefreshing: Boolean) {

    @Immutable
    data class SeasonNotStartedViewState(
        val futureRaces: List<Race>,
        override val isRefreshing: Boolean
    ) : RacesViewState(isRefreshing)

    @Immutable
    data class SeasonInProgressViewState(
        val pastRaces: List<Race>,
        val todayStages: List<TodayStage>,
        val futureRaces: List<Race>,
        override val isRefreshing: Boolean
    ) : RacesViewState(isRefreshing)

    @Immutable
    data class SeasonEndedViewState(val pastRaces: List<Race>, override val isRefreshing: Boolean) :
        RacesViewState(isRefreshing)

    object EmptyViewState : RacesViewState(false)
    companion object {
        val Empty = EmptyViewState
    }
}
