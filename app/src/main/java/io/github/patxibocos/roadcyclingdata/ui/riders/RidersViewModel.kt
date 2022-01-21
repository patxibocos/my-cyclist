package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(private val ridersRepository: RidersRepository) :
    ViewModel() {

    private val search = MutableStateFlow("")

    init {
        viewModelScope.launch {
            ridersRepository.filterRiders("")
        }
    }

    val state: StateFlow<State> = combine(ridersRepository.riders, search) { riders, query ->
        State(riders, query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = State.Empty,
    )

    fun onSearched(query: String) {
        viewModelScope.launch {
            search.emit(query)
            ridersRepository.filterRiders(query)
        }
    }
}

data class State(val riders: List<Rider>, val search: String) {
    companion object {
        val Empty = State(emptyList(), "")
    }
}
