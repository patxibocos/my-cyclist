package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
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
import io.github.patxibocos.mycyclist.data.isFinished
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
import java.time.LocalDate
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
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
                _resultsMode.emit(ResultsMode.GcResults)
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
                        resultsMode = ResultsMode.GcResults
                    }
                    race.todayStage() != null -> {
                        stageIndex = race.todayStage()!!.second
                        resultsMode = ResultsMode.StageResults
                    }
                    !race.isFinished() -> {
                        stageIndex = race.indexOfLastStageWithResults()
                        resultsMode = ResultsMode.GcResults
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
            _resultsMode
        ) { races, riders, teams, stageIndex, resultsMode ->
            val race = races.find { it.id == raceId }!!
            val stageResults = race.stages.associateWith { stage ->
                StageResults(
                    result = when (stage.stageType) {
                        StageType.TEAM_TIME_TRIAL -> stage.result.map { participantResult ->
                            ParticipantResult.TeamResult(
                                teams.find { it.id == participantResult.participantId }!!,
                                participantResult.time
                            )
                        }
                        else -> stage.result.map { riderResult ->
                            ParticipantResult.RiderResult(
                                riders.find { it.id == riderResult.participantId }
                                    ?: buildDummyRider(
                                        riderResult.participantId
                                    ),
                                riderResult.time
                            )
                        }
                    },
                    gcResult = stage.gcResult.map { riderResult ->
                        ParticipantResult.RiderResult(
                            riders.find { it.id == riderResult.participantId } ?: buildDummyRider(
                                riderResult.participantId
                            ),
                            riderResult.time
                        )
                    }
                )
            }
            RaceViewState(race, stageIndex, resultsMode, stageResults)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = RaceViewState.Empty
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

private fun buildDummyRider(riderId: String): Rider {
    val splits = riderId.split("-")
    val firstName = splits.first().capitalize(Locale.current)
    val lastName = splits.drop(1).joinToString(" ") { it.capitalize(Locale.current) }
    return Rider(
        id = "", // Leaving this empty intentionally so we now this is a dummy rider
        firstName = firstName,
        lastName = lastName,
        photo = "",
        country = "",
        website = "",
        birthDate = LocalDate.MIN,
        birthPlace = "",
        weight = 0,
        height = 0,
        uciRankingPosition = 0
    )
}

@Immutable
enum class ResultsMode {
    StageResults,
    GcResults
}

@Immutable
data class RaceViewState(
    val race: Race?,
    val currentStageIndex: Int,
    val resultsMode: ResultsMode,
    val stageResults: Map<Stage, StageResults>
) {
    companion object {
        val Empty = RaceViewState(
            race = null,
            currentStageIndex = 0,
            resultsMode = ResultsMode.StageResults,
            stageResults = emptyMap()
        )
    }
}

@Immutable
data class StageResults(
    val result: List<ParticipantResult>,
    val gcResult: List<ParticipantResult.RiderResult>
)

@Immutable
sealed class ParticipantResult(open val time: Long) {
    @Immutable
    data class RiderResult(val rider: Rider, override val time: Long) : ParticipantResult(time)

    @Immutable
    data class TeamResult(val team: Team, override val time: Long) : ParticipantResult(time)
}
