package io.github.patxibocos.roadcyclingdata.ui.races

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    private val _races: MutableStateFlow<List<Race>> = MutableStateFlow(emptyList())
    val races: StateFlow<List<Race>> = _races

    init {
        viewModelScope.launch(Dispatchers.Default) {
            dataRepository.races().collect { races ->
                _races.emit(races)
            }
        }
    }
}
