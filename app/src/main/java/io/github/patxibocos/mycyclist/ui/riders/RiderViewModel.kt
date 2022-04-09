package io.github.patxibocos.mycyclist.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.ui.data.RiderOfTeam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _riderId = MutableStateFlow("")

    val riderOfTeam: Flow<RiderOfTeam?> =
        combine(_riderId, dataRepository.teams, dataRepository.riders) { riderId, teams, riders ->
            val rider = riders.find { it.id == riderId }
            val team = teams.find { it.riderIds.contains(riderId) }
            if (rider != null && team != null) {
                RiderOfTeam(rider, team)
            } else {
                null
            }
        }

    fun loadRider(riderId: String) {
        _riderId.value = riderId
    }
}
