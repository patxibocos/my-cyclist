package io.github.patxibocos.mycyclist.ui.riders

import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.data.rider
import io.github.patxibocos.mycyclist.ui.data.team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RidersViewModelTest {

    @Test
    fun search() = runBlocking {
        val rider = rider(id = "riderId", lastName = "aBc")
        val team = team(riderIds = listOf(rider.id))
        val dataRepository = object : DataRepository {
            override val teams: Flow<List<Team>>
                get() = flow { emit(listOf(team)) }
            override val riders: Flow<List<Rider>>
                get() = flow { emit(listOf(rider)) }
            override val races: Flow<List<Race>>
                get() = flow { emit(emptyList()) }
        }
        val viewModel = RidersViewModel(dataRepository)

        viewModel.onSorted(Sorting.Team)
        viewModel.onSearched("AbC")

        assertEquals(
            UiState(UiState.UiRiders.RidersByTeam(mapOf(team to listOf(rider))), "AbC"),
            viewModel.state.first()
        )
    }
}
