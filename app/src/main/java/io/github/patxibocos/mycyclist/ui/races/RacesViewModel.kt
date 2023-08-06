package io.github.patxibocos.mycyclist.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.StageType
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.endDate
import io.github.patxibocos.mycyclist.data.firstStage
import io.github.patxibocos.mycyclist.data.isActive
import io.github.patxibocos.mycyclist.data.isFuture
import io.github.patxibocos.mycyclist.data.isPast
import io.github.patxibocos.mycyclist.data.isSingleDay
import io.github.patxibocos.mycyclist.data.startDate
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
        combine(
            dataRepository.races,
            dataRepository.teams,
            dataRepository.riders,
            _refreshing,
        ) { races, teams, riders, refreshing ->
            val minStartDate = races.first().startDate()
            val maxEndDate = races.last().endDate()
            val today = LocalDate.now(ZoneId.systemDefault())
            when {
                today.isBefore(minStartDate) -> RacesViewState.SeasonNotStartedViewState(
                    races,
                    refreshing,
                )

                today.isAfter(maxEndDate) -> RacesViewState.SeasonEndedViewState(
                    races,
                    refreshing,
                )

                else -> {
                    val todayStages = races.filter(Race::isActive).map { race ->
                        val todayStage = race.todayStage()
                        when {
                            race.isSingleDay() -> TodayStage.SingleDayRace(
                                race = race,
                                stage = race.firstStage(),
                                results = stageResults(race.firstStage(), riders, teams),
                            )

                            todayStage != null -> TodayStage.MultiStageRace(
                                race = race,
                                stage = todayStage.first,
                                stageNumber = todayStage.second + 1,
                                results = stageResults(todayStage.first, riders, teams),
                            )

                            else -> TodayStage.RestDay(race)
                        }
                    }
                    RacesViewState.SeasonInProgressViewState(
                        todayStages = todayStages,
                        pastRaces = races.filter(Race::isPast).reversed(),
                        futureRaces = races.filter(Race::isFuture),
                        isRefreshing = refreshing,
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RacesViewState.Empty,
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
    data class SingleDayRace(
        override val race: Race,
        val stage: Stage,
        val results: TodayResults,
    ) : TodayStage(race)

    data class MultiStageRace(
        override val race: Race,
        val stage: Stage,
        val stageNumber: Int,
        val results: TodayResults,
    ) :
        TodayStage(race)
}

@Immutable
sealed class RacesViewState(open val isRefreshing: Boolean) {

    @Immutable
    data class SeasonNotStartedViewState(
        val futureRaces: List<Race>,
        override val isRefreshing: Boolean,
    ) : RacesViewState(isRefreshing)

    @Immutable
    data class SeasonInProgressViewState(
        val pastRaces: List<Race>,
        val todayStages: List<TodayStage>,
        val futureRaces: List<Race>,
        override val isRefreshing: Boolean,
    ) : RacesViewState(isRefreshing)

    @Immutable
    data class SeasonEndedViewState(val pastRaces: List<Race>, override val isRefreshing: Boolean) :
        RacesViewState(isRefreshing)

    data object EmptyViewState : RacesViewState(false)
    companion object {
        val Empty = EmptyViewState
    }
}

sealed interface TodayResults {
    data class Teams(val teams: List<TeamTimeResult>) : TodayResults
    data class Riders(val riders: List<RiderTimeResult>) : TodayResults
}

private fun stageResults(stage: Stage, riders: List<Rider>, teams: List<Team>): TodayResults {
    return when (stage.stageType) {
        StageType.REGULAR, StageType.INDIVIDUAL_TIME_TRIAL -> TodayResults.Riders(
            stage.stageResults.time.take(3).map { participantResult ->
                RiderTimeResult(
                    riders.find { it.id == participantResult.participantId }!!,
                    participantResult.time,
                )
            },
        )

        StageType.TEAM_TIME_TRIAL -> TodayResults.Teams(
            stage.stageResults.time.take(3).map { participantResult ->
                TeamTimeResult(
                    teams.find { it.id == participantResult.participantId }!!,
                    participantResult.time,
                )
            },
        )
    }
}
