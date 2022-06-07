package io.github.patxibocos.mycyclist.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.DefaultDispatcher
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.areResultsAvailable
import io.github.patxibocos.mycyclist.data.hasMultipleStages
import io.github.patxibocos.mycyclist.ui.data.Participation
import io.github.patxibocos.mycyclist.ui.data.Result
import io.github.patxibocos.mycyclist.ui.data.RiderDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(
    dataRepository: DataRepository,
    @DefaultDispatcher val defaultDispatcher: CoroutineDispatcher
) :
    ViewModel() {

    private val _riderId = MutableStateFlow("")

    val riderDetails: Flow<RiderDetails?> =
        combine(
            _riderId,
            dataRepository.teams,
            dataRepository.riders,
            dataRepository.races
        ) { riderId, teams, riders, races ->
            getRiderDetails(defaultDispatcher, riderId, riders, teams, races)
        }

    fun loadRider(riderId: String) {
        _riderId.value = riderId
    }
}

suspend fun getRiderDetails(
    defaultDispatcher: CoroutineDispatcher,
    riderId: String,
    riders: List<Rider>,
    teams: List<Team>,
    races: List<Race>
): RiderDetails? = withContext(defaultDispatcher) {
    fun riderParticipations(riderId: String, races: List<Race>): List<Participation> =
        races.mapNotNull { race ->
            race.teamParticipations.flatMap { it.riderParticipations } // Flattening this because team IDs may change on PCS
                .find { it.riderId == riderId }
                ?.let { Participation(race, it.number) }
        }

    fun riderResults(riderId: String, participations: List<Participation>): List<Result> =
        participations.map { it.race }.filter(Race::areResultsAvailable)
            .mapNotNull { race ->
                race.result.take(3).find { it.riderId == riderId }
                    ?.let { Result.RaceResult(race, it.position) }
            } + participations.map { it.race }.filter(Race::hasMultipleStages)
            .flatMap { race ->
                race.stages.mapNotNull { stage ->
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
            }

    val rider = riders.find { it.id == riderId }
    val team = teams.find { it.riderIds.contains(riderId) }
    if (rider != null && team != null) {
        val participations = riderParticipations(riderId, races)
        val results = riderResults(riderId, participations)
        RiderDetails(rider, team, participations, results)
    } else {
        null
    }
}
