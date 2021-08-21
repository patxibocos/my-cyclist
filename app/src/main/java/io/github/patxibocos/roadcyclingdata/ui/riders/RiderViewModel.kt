package io.github.patxibocos.roadcyclingdata.ui.riders

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Rider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

@HiltViewModel
class RiderViewModel @Inject constructor(private val dataRepository: DataRepository) :
    ViewModel() {

    fun getRider(riderId: String): Flow<Rider> =
        dataRepository.riders().mapNotNull { riders -> riders.find { it.id == riderId } }
}
