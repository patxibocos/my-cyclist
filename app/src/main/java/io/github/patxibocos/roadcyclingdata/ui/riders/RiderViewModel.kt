package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.ui.data.RiderOfTeam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRiderOfTeam(riderId: String): Flow<RiderOfTeam> =
        dataRepository.riders().mapNotNull { riders -> riders.find { it.id == riderId } }
            .flatMapLatest { rider ->
                dataRepository.teams()
                    .mapNotNull { teams -> teams.find { it.riderIdsList.contains(riderId) } }
                    .map { team -> RiderOfTeam(rider, team) }
            }
}
