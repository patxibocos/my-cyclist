package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

    val state: Flow<UiState> =
        combine(ridersRepository.riders, search) { riders, query -> UiState(riders, query) }

    fun onSearched(query: String) {
        viewModelScope.launch {
            search.emit(query)
            ridersRepository.filterRiders(query)
        }
    }
}

data class UiState(val riders: List<Rider>, val search: String) {
    companion object {
        val Empty = UiState(emptyList(), "")
    }
}
