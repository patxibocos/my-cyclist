package io.github.patxibocos.mycyclist.ui.riders

import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.data.rider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RidersViewModelTest {

    @Test
    fun search() = runBlocking {
        val rider = rider(id = "riderId", firstName = "Patxi", lastName = "Bocos")
        val dataRepository = object : DataRepository {
            override val riders: Flow<List<Rider>>
                get() = flowOf(listOf(rider))
            override val teams: Flow<List<Team>>
                get() = flowOf(emptyList())
            override val races: Flow<List<Race>>
                get() = flowOf(emptyList())
        }
        val viewModel = RidersViewModel(dataRepository)

        val searchQuery = "pa bo"
        viewModel.onSorted(Sorting.LastName)
        viewModel.onSearched(searchQuery)

        val state = viewModel.state.first()
        assertEquals(UiState.UiRiders.RidersByLastName(mapOf('B' to listOf(rider))), state.riders)
        assertEquals(searchQuery, state.search)
    }
}
