package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    val races = liveData {
        dataRepository.races().collect {
            emit(it)
        }
    }
}
