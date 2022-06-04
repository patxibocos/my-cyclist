package io.github.patxibocos.mycyclist.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.ui.data.Participation
import io.github.patxibocos.mycyclist.ui.data.RiderDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _riderId = MutableStateFlow("")

    val riderDetails: Flow<RiderDetails?> =
        combine(
            _riderId,
            dataRepository.teams,
            dataRepository.riders,
            dataRepository.races
        ) { riderId, teams, riders, races ->
            val rider = riders.find { it.id == riderId }
            val team = teams.find { it.riderIds.contains(riderId) }
            if (rider != null && team != null) {
                val participations = races.mapNotNull { race ->
                    race.teamParticipations.find { it.teamId == team.id }?.riderParticipations?.find { it.riderId == riderId }
                        ?.let { Participation(race, it.number) }
                }
                RiderDetails(rider, team, participations, emptyList())
            } else {
                null
            }
        }

    fun loadRider(riderId: String) {
        _riderId.value = riderId
    }
}
