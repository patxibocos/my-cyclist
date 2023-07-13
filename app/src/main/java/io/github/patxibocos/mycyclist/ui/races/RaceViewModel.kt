package io.github.patxibocos.mycyclist.ui.races

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
) :
    ViewModel() {

    private val raceId: String = savedStateHandle["raceId"]!!
    private val stageId: String? = savedStateHandle["stageId"]

    private val _stageIndex = MutableSharedFlow<Int>(replay = 1)
    private val _resultsMode = MutableSharedFlow<ResultsMode>(replay = 1)

    init {
        viewModelScope.launch {
            val race = dataRepository.races.first().find { it.id == raceId }!!
            if (race.isSingleDay()) {
                _stageIndex.emit(0)
                _resultsMode.emit(ResultsMode.GeneralResults)
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
                resultsMode = ResultsMode.StageResults
            } else {
                when {
                    race.isPast() -> {
                        stageIndex = race.stages.size - 1
                        resultsMode = ResultsMode.GeneralResults
                    }

                    race.todayStage() != null -> {
                        stageIndex = race.todayStage()!!.second
                        resultsMode = ResultsMode.StageResults
                    }

                    race.isActive() -> {
                        stageIndex = race.indexOfLastStageWithResults()
                        resultsMode = ResultsMode.GeneralResults
                    }

                    else -> {
                        stageIndex = 0
                        resultsMode = ResultsMode.StageResults
                    }
                }
            }
            _stageIndex.emit(stageIndex)
            _resultsMode.emit(resultsMode)
        }
    }

    val raceViewState: StateFlow<RaceViewState> =
        combine(
            dataRepository.races,
            dataRepository.riders,
            dataRepository.teams,
            _stageIndex,
            _resultsMode,
        ) { races, riders, teams, stageIndex, resultsMode ->
            val race = races.find { it.id == raceId }!!
            val stageResults = race.stages.associateWith { stage ->
                StageResults(
                    result = when (stage.stageType) {
                        StageType.TEAM_TIME_TRIAL -> stage.stageResults.time.map { participantResult ->
                            ParticipantResult.TeamResult(
                                teams.find { it.id == participantResult.participantId }!!,
                                participantResult.time,
                            )
                        }

                        else -> stage.stageResults.time.map { riderResult ->
                            ParticipantResult.RiderResult(
                                riders.find { it.id == riderResult.participantId }!!,
                                riderResult.time,
                            )
                        }
                    },
                    gcResult = stage.generalResults.time.map { riderResult ->
                        ParticipantResult.RiderResult(
                            riders.find { it.id == riderResult.participantId }!!,
                            riderResult.time,
                        )
                    },
                )
            }
            RaceViewState(race, stageIndex, resultsMode, stageResults)
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
}

@Immutable
enum class ResultsMode {
    StageResults,
    GeneralResults,
}

@Immutable
data class RaceViewState(
    val race: Race?,
    val currentStageIndex: Int,
    val resultsMode: ResultsMode,
    val stageResults: Map<Stage, StageResults>,
) {
    companion object {
        val Empty = RaceViewState(
            race = null,
            currentStageIndex = 0,
            resultsMode = ResultsMode.StageResults,
            stageResults = emptyMap(),
        )
    }
}

@Immutable
data class StageResults(
    val result: List<ParticipantResult>,
    val gcResult: List<ParticipantResult.RiderResult>,
)

@Immutable
sealed class ParticipantResult(open val time: Long) {
    @Immutable
    data class RiderResult(val rider: Rider, override val time: Long) : ParticipantResult(time)

    @Immutable
    data class TeamResult(val team: Team, override val time: Long) : ParticipantResult(time)
}
