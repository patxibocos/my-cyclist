package io.github.patxibocos.mycyclist.ui.riders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.endDate
import io.github.patxibocos.mycyclist.data.isSingleDay
import io.github.patxibocos.mycyclist.data.result
import io.github.patxibocos.mycyclist.data.startDate
import io.github.patxibocos.mycyclist.ui.data.Participation
import io.github.patxibocos.mycyclist.ui.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher,
) :
    ViewModel() {

    private val riderId: String = savedStateHandle["riderId"]!!

    val riderViewState: StateFlow<RiderViewState> =
        combine(
            dataRepository.teams,
            dataRepository.riders,
            dataRepository.races,
        ) { teams, riders, races ->
            val rider = riders.find { it.id == riderId }
            val team = teams.find { it.riderIds.contains(riderId) }
            val (pastParticipations, currentParticipation, futureParticipations) = riderParticipations(
                defaultDispatcher,
                riderId,
                races,
            )
            val results = riderResults(
                defaultDispatcher,
                riderId,
                pastParticipations + listOfNotNull(currentParticipation),
            )
            RiderViewState(
                rider,
                team,
                currentParticipation,
                pastParticipations,
                futureParticipations,
                results,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RiderViewState.Empty,
        )
}

suspend fun riderParticipations(
    defaultDispatcher: CoroutineDispatcher,
    riderId: String,
    races: List<Race>,
): Triple<List<Participation>, Participation?, List<Participation>> {
    return withContext(defaultDispatcher) {
        val participations = races.mapNotNull { race ->
            race.teamParticipations.flatMap { it.riderParticipations } // Flattening this because team IDs may change on PCS
                .find { it.riderId == riderId }
                ?.let { Participation(race, it.number) }
        }
        val today = LocalDate.now(ZoneId.systemDefault())
        val currentParticipation =
            participations.find { it.race.startDate() <= today && it.race.endDate() >= today }
        val pastParticipations = participations.filter { it.race.endDate() < today }
        val futureParticipations = participations.filter { it.race.startDate() > today }
        Triple(pastParticipations, currentParticipation, futureParticipations)
    }
}

suspend fun riderResults(
    defaultDispatcher: CoroutineDispatcher,
    riderId: String,
    participations: List<Participation>,
): List<Result> {
    return withContext(defaultDispatcher) {
        participations.map { it.race }
            .flatMap { race ->
                val raceResult = race.result()?.take(3)?.find { it.participantId == riderId }
                    ?.let { Result.RaceResult(race, it.position) }
                if (race.isSingleDay()) {
                    return@flatMap listOfNotNull(raceResult)
                }
                val stageResults = race.stages.mapNotNull { stage ->
                    stage.stageResults.time.take(3).find { it.participantId == riderId }
                        ?.let {
                            Result.StageResult(
                                race,
                                stage,
                                race.stages.indexOf(stage) + 1,
                                it.position,
                            )
                        }
                }
                return@flatMap stageResults + listOfNotNull(raceResult)
            }
    }
}

@Immutable
data class RiderViewState(
    val rider: Rider?,
    val team: Team?,
    val currentParticipation: Participation?,
    val pastParticipations: List<Participation>,
    val futureParticipations: List<Participation>,
    val results: List<Result>,
) {
    companion object {
        val Empty = RiderViewState(
            rider = null,
            team = null,
            currentParticipation = null,
            pastParticipations = emptyList(),
            futureParticipations = emptyList(),
            results = emptyList(),
        )
    }
}
