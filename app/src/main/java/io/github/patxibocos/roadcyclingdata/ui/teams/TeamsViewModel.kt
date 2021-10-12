package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    val teams = liveData {
        dataRepository.teams().collect {
            emit(it)
        }
    }
}
