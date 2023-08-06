package io.github.patxibocos.mycyclist.ui.races

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Place
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.StageType
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.indexOfLastStageWithResults
import io.github.patxibocos.mycyclist.data.isActive
import io.github.patxibocos.mycyclist.data.isPast
import io.github.patxibocos.mycyclist.data.isSingleDay
import io.github.patxibocos.mycyclist.data.todayStage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!
    private val stageId: String? = savedStateHandle["stageId"]

    private val _stageIndex = MutableSharedFlow<Int>(replay = 1)
    private val _resultsMode = MutableSharedFlow<ResultsMode>(replay = 1)
    private val _classificationType = MutableSharedFlow<ClassificationType>(replay = 1)

    init {
        viewModelScope.launch {
            val race = dataRepository.races.first().find { it.id == raceId }!!
            if (race.isSingleDay()) {
                _stageIndex.emit(0)
                _resultsMode.emit(ResultsMode.General)
                _classificationType.emit(ClassificationType.Time)
                return@launch
            }
            val stageIndex: Int
            val resultsMode: ResultsMode
            // If stageId is not provided:
            //   Is past race -> set last stage as current stage + set GC view as current view
            //   Today is happening any of the stages -> set today's stage as current stage + set Stage view as current view
            //   Today is rest day -> set yesterday's stage as current stage + set GC view as current view
            //   Else -> set first stage as current stage + set Stage view as current stage
            // Otherwise
            //   Set given stage as current stage + set Stage view as current view
            if (stageId != null) {
                stageIndex = race.stages.indexOfFirst { it.id == stageId }
                resultsMode = ResultsMode.Stage
            } else {
                when {
                    race.isPast() -> {
                        stageIndex = race.stages.size - 1
                        resultsMode = ResultsMode.General
                    }

                    race.todayStage() != null -> {
                        stageIndex = race.todayStage()!!.second
                        resultsMode = ResultsMode.Stage
                    }

                    race.isActive() -> {
                        stageIndex = race.indexOfLastStageWithResults()
                        resultsMode = ResultsMode.General
                    }

                    else -> {
                        stageIndex = 0
                        resultsMode = ResultsMode.Stage
                    }
                }
            }
            _stageIndex.emit(stageIndex)
            _resultsMode.emit(resultsMode)
            _classificationType.emit(ClassificationType.Time)
        }
    }

    val raceViewState: StateFlow<RaceViewState> =
        combine(
            dataRepository.races,
            dataRepository.riders,
            dataRepository.teams,
            _stageIndex,
            combine(
                _resultsMode,
                _classificationType,
            ) { resultsMode, classificationType -> resultsMode to classificationType },
        ) { races, riders, teams, stageIndex, (resultsMode, classificationType) ->
            val race = races.find { it.id == raceId }!!
            val stagesResults = race.stages.associateWith { stage ->
                stageResults(
                    defaultDispatcher,
                    stage,
                    resultsMode,
                    classificationType,
                    riders,
                    teams,
                )
            }
            RaceViewState(race, stageIndex, resultsMode, classificationType, stagesResults)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RaceViewState.Empty,
        )

    fun onStageSelected(stageIndex: Int) {
        viewModelScope.launch {
            _stageIndex.emit(stageIndex)
        }
    }

    fun onResultsModeChanged(resultsMode: ResultsMode) {
        viewModelScope.launch {
            _resultsMode.emit(resultsMode)
        }
    }

    fun onClassificationTypeChanged(classificationType: ClassificationType) {
        viewModelScope.launch {
            _classificationType.emit(classificationType)
        }
    }
}

private suspend fun stageResults(
    defaultDispatcher: CoroutineDispatcher,
    stage: Stage,
    resultsMode: ResultsMode,
    classificationType: ClassificationType,
    riders: List<Rider>,
    teams: List<Team>,
): Results {
    return withContext(defaultDispatcher) {
        when (classificationType) {
            ClassificationType.Time -> when (resultsMode) {
                ResultsMode.Stage -> when (stage.stageType) {
                    StageType.TEAM_TIME_TRIAL -> Results.TeamsTimeResult(
                        stage.stageResults.time.map { participantResult ->
                            TeamTimeResult(
                                teams.find { it.id == participantResult.participantId }!!,
                                participantResult.time,
                            )
                        },
                    )

                    else -> Results.RidersTimeResult(
                        stage.stageResults.time.map { participantResult ->
                            RiderTimeResult(
                                riders.find { it.id == participantResult.participantId }!!,
                                participantResult.time,
                            )
                        },
                    )
                }

                ResultsMode.General -> Results.RidersTimeResult(
                    stage.generalResults.time.map { participantResult ->
                        RiderTimeResult(
                            riders.find { it.id == participantResult.participantId }!!,
                            participantResult.time,
                        )
                    },
                )
            }

            ClassificationType.Points -> when (resultsMode) {
                ResultsMode.Stage -> Results.RidersPointsPerPlaceResult(
                    stage.stageResults.points.associate {
                        it.place to it.points.map { riderResult ->
                            RiderPointsResult(
                                riders.find { rider -> rider.id == riderResult.participant }!!,
                                riderResult.points,
                            )
                        }
                    },
                )

                ResultsMode.General -> Results.RidersPointResult(
                    stage.generalResults.points.map { participantResult ->
                        RiderPointsResult(
                            riders.find { it.id == participantResult.participant }!!,
                            participantResult.points,
                        )
                    },
                )
            }

            ClassificationType.Kom -> when (resultsMode) {
                ResultsMode.Stage -> Results.RidersPointsPerPlaceResult(
                    stage.stageResults.kom.associate {
                        it.place to it.points.map { riderResult ->
                            RiderPointsResult(
                                riders.find { rider -> rider.id == riderResult.participant }!!,
                                riderResult.points,
                            )
                        }
                    },
                )

                ResultsMode.General -> Results.RidersPointResult(
                    stage.generalResults.kom.map { participantResult ->
                        RiderPointsResult(
                            riders.find { it.id == participantResult.participant }!!,
                            participantResult.points,
                        )
                    },
                )
            }

            ClassificationType.Youth -> when (resultsMode) {
                ResultsMode.Stage -> Results.RidersTimeResult(
                    stage.stageResults.youth.map { participantResult ->
                        RiderTimeResult(
                            riders.find { it.id == participantResult.participantId }!!,
                            participantResult.time,
                        )
                    },
                )

                ResultsMode.General -> Results.RidersTimeResult(
                    stage.generalResults.youth.map { participantResult ->
                        RiderTimeResult(
                            riders.find { it.id == participantResult.participantId }!!,
                            participantResult.time,
                        )
                    },
                )
            }

            ClassificationType.Teams -> when (resultsMode) {
                ResultsMode.Stage -> Results.TeamsTimeResult(
                    stage.stageResults.teams.map { participantResult ->
                        TeamTimeResult(
                            teams.find { it.id == participantResult.participantId }!!,
                            participantResult.time,
                        )
                    },
                )

                ResultsMode.General -> Results.TeamsTimeResult(
                    stage.generalResults.teams.map { participantResult ->
                        TeamTimeResult(
                            teams.find { it.id == participantResult.participantId }!!,
                            participantResult.time,
                        )
                    },
                )
            }
        }
    }
}

@Immutable
enum class ClassificationType {
    Time,
    Points,
    Kom,
    Youth,
    Teams,
}

@Immutable
enum class ResultsMode {
    Stage,
    General,
}

@Immutable
data class RaceViewState(
    val race: Race?,
    val currentStageIndex: Int,
    val resultsMode: ResultsMode,
    val classificationType: ClassificationType,
    val stagesResults: Map<Stage, Results>,
) {
    companion object {
        val Empty = RaceViewState(
            race = null,
            currentStageIndex = 0,
            resultsMode = ResultsMode.Stage,
            classificationType = ClassificationType.Time,
            stagesResults = emptyMap(),
        )
    }
}

@Immutable
sealed interface Results {
    @Immutable
    data class TeamsTimeResult(val teams: List<TeamTimeResult>) : Results

    @Immutable
    data class RidersTimeResult(val riders: List<RiderTimeResult>) : Results

    @Immutable
    data class RidersPointResult(val riders: List<RiderPointsResult>) : Results

    @Immutable
    data class RidersPointsPerPlaceResult(val perPlaceResult: Map<Place, List<RiderPointsResult>>) :
        Results
}

@Immutable
data class RiderTimeResult(val rider: Rider, val time: Long)

@Immutable
data class TeamTimeResult(val team: Team, val time: Long)

@Immutable
data class RiderPointsResult(
    val rider: Rider,
    val points: Int,
)
