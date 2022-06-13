package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.isSingleDay
import io.github.patxibocos.mycyclist.ui.data.Participation
import io.github.patxibocos.mycyclist.ui.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(
    dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher
) :
    ViewModel() {

    private val riderId: String = savedStateHandle["riderId"]!!

    val riderViewState: Flow<RiderViewState> =
        combine(
            dataRepository.teams,
            dataRepository.riders,
            dataRepository.races
        ) { teams, riders, races ->
            val rider = riders.find { it.id == riderId }
            val team = teams.find { it.riderIds.contains(riderId) }
            val (pastParticipations, currentParticipation, futureParticipations) = riderParticipations(
                defaultDispatcher,
                riderId,
                races
            )
            val results = riderResults(
                defaultDispatcher,
                riderId,
                pastParticipations + listOfNotNull(currentParticipation)
            )
            RiderViewState(
                rider,
                team,
                currentParticipation,
                pastParticipations,
                futureParticipations,
                results
            )
        }
}

suspend fun riderParticipations(
    defaultDispatcher: CoroutineDispatcher,
    riderId: String,
    races: List<Race>
): Triple<List<Participation>, Participation?, List<Participation>> {
    return withContext(defaultDispatcher) {
        val participations = races.mapNotNull { race ->
            race.teamParticipations.flatMap { it.riderParticipations } // Flattening this because team IDs may change on PCS
                .find { it.riderId == riderId }
                ?.let { Participation(race, it.number) }
        }
        val today = LocalDate.now()
        val currentParticipation =
            participations.find { it.race.startDate <= today && it.race.endDate >= today }
        val pastParticipations = participations.filter { it.race.endDate < today }
        val futureParticipations = participations.filter { it.race.startDate > today }
        Triple(pastParticipations, currentParticipation, futureParticipations)
    }
}

suspend fun riderResults(
    defaultDispatcher: CoroutineDispatcher,
    riderId: String,
    participations: List<Participation>
): List<Result> {
    return withContext(defaultDispatcher) {
        participations.map { it.race }
            .flatMap { race ->
                listOfNotNull(
                    race.result.take(3).find { it.riderId == riderId }
                        ?.let { Result.RaceResult(race, it.position) }
                ).run {
                    if (!race.isSingleDay()) {
                        this + race.stages.mapNotNull { stage ->
                            stage.result.take(3).find { it.riderId == riderId }
                                ?.let {
                                    Result.StageResult(
                                        race,
                                        stage,
                                        race.stages.indexOf(stage) + 1,
                                        it.position
                                    )
                                }
                        }
                    } else {
                        this
                    }
                }
            }
    }
}

@Immutable
@Stable
data class RiderViewState(
    val rider: Rider? = null,
    val team: Team? = null,
    val currentParticipation: Participation? = null,
    val pastParticipations: List<Participation> = emptyList(),
    val futureParticipations: List<Participation> = emptyList(),
    val results: List<Result> = emptyList(),
) {
    companion object {
        val Empty = RiderViewState()
    }
}
