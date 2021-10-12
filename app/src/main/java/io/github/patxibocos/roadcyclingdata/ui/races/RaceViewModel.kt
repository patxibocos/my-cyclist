package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class RaceViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _raceId = MutableLiveData<String>()

    val race = _raceId.switchMap { raceId ->
        liveData {
            dataRepository.races().collect { races ->
                val race = races.find { it.id == raceId }
                emit(race)
            }
        }
    }

    fun loadRace(raceId: String) {
        _raceId.value = raceId
    }
}
