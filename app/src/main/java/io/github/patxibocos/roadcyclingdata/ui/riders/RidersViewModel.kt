package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.db.Rider
import io.github.patxibocos.roadcyclingdata.data.db.RiderDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidersViewModel @Inject constructor(private val riderDao: RiderDao) : ViewModel() {

    private var allRiders: List<Rider> = emptyList()
    private val _riders: MutableStateFlow<List<Rider>> = MutableStateFlow(emptyList())
    val riders: StateFlow<List<Rider>> = _riders

    init {
        viewModelScope.launch(Dispatchers.IO) {
            allRiders = riderDao.getRiders().sortedWith(
                compareBy({ it.firstName }, { it.lastName })
            )
            _riders.emit(allRiders)
        }
    }

    private fun reloadRiders(query: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val filteredRiders = filterRiders(query)
            _riders.emit(filteredRiders)
        }
    }

    private fun filterRiders(query: String): List<Rider> {
        val querySplits = query.trim().split(" ").map { it.trim() }
        return allRiders.filter { rider ->
            // For each of the split, it should be contained either on first or last name
            querySplits.all { q ->
                rider.firstName.contains(q, ignoreCase = true)
                        || rider.lastName.contains(q, ignoreCase = true)
            }
        }
    }

    fun onSearched(query: String) {
        reloadRiders(query)
    }

}