package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.ui.data.RiderOfTeam
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    private val _riderId = MutableSharedFlow<String>()

    val riderOfTeam: StateFlow<RiderOfTeam?> =
        combine(
            _riderId,
            dataRepository.teams,
            dataRepository.riders
        ) { riderId, teams, riders ->
            val rider = riders.find { it.id == riderId }
            val team = teams.find { it.riderIdsList.contains(riderId) }
            return@combine if (rider != null && team != null) {
                RiderOfTeam(rider, team)
            } else {
                null
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    fun loadRider(riderId: String) {
        viewModelScope.launch {
            _riderId.emit(riderId)
        }
    }
}
