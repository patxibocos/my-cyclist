package io.github.patxibocos.roadcyclingdata.ui.riders

import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class RidersRepository @Inject constructor(private val dataRepository: DataRepository) {
    private val _riders: MutableStateFlow<List<Rider>> = MutableStateFlow(emptyList())
    val riders: Flow<List<Rider>> = _riders

    private fun List<Rider>.filter(query: String): List<Rider> {
        val querySplits = query.trim().split(" ").map { it.trim() }
        return this.filter { rider ->
            // For each of the split, it should be contained either on first or last name
            querySplits.all { q ->
                rider.firstName.contains(
                    q,
                    ignoreCase = true
                ) || rider.lastName.contains(q, ignoreCase = true)
            }
        }
    }

    suspend fun filterRiders(query: String) {
        dataRepository.riders.collect {
            _riders.emit(it.filter(query))
        }
    }
}
