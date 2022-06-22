package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class RacesViewModel @Inject constructor(dataRepository: DataRepository) :
    ViewModel() {

    val racesViewState: Flow<RacesViewState> = dataRepository.races.map(::RacesViewState)
}

@Immutable
@Stable
data class RacesViewState(val races: List<Race> = emptyList()) {
    companion object {
        val Empty = RacesViewState()
    }
}
