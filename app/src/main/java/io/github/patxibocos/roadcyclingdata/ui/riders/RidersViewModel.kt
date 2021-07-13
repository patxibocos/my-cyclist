package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.json.DataProvider
import io.github.patxibocos.roadcyclingdata.data.json.Rider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(private val dataProvider: DataProvider) : ViewModel() {

    private val _riders: MutableStateFlow<List<Rider>> = MutableStateFlow(emptyList())
    val riders: StateFlow<List<Rider>> = _riders

    init {
        viewModelScope.launch {
            dataProvider.riders().collect {
                _riders.emit(it)
            }
        }
    }

    private fun reloadRiders(query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val querySplits = query.trim().split(" ").map { it.trim() }
            dataProvider.riders().collect { riders ->
                val filteredResults = riders.filter { rider ->
                    // For each of the split, it should be contained either on first or last name
                    querySplits.all { q ->
                        rider.firstName.contains(q, ignoreCase = true)
                                || rider.lastName.contains(q, ignoreCase = true)
                    }
                }
                _riders.emit(filteredResults)
            }
        }
    }

    fun onSearched(query: String) {
        reloadRiders(query)
    }

}