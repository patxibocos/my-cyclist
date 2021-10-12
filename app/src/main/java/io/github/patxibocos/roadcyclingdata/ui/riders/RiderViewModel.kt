package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.ui.data.RiderOfTeam
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _riderId = MutableLiveData<String>()

    val riderOfTeam = _riderId.switchMap { riderId ->
        liveData {
            combine(dataRepository.teams(), dataRepository.riders()) { teams, riders ->
                teams to riders
            }.collect { (teams, riders) ->
                val rider = riders.find { it.id == riderId }
                val team = teams.find { it.riderIdsList.contains(riderId) }
                if (rider != null && team != null) {
                    val riderOfTeam = RiderOfTeam(rider, team)
                    emit(riderOfTeam)
                }
            }
        }
    }

    fun loadRider(riderId: String) {
        _riderId.value = riderId
    }
}
