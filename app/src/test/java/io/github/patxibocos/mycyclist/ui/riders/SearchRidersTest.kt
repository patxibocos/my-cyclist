package io.github.patxibocos.mycyclist.ui.riders

import io.github.patxibocos.mycyclist.ui.data.rider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchRidersTest {

    @Test
    fun search() = runBlocking {
        val riders = listOf(rider(id = "riderId", firstName = "Patxi", lastName = "Bocos"))

        val searchQuery = "pa bo"
        val searchResult = searchRiders(Dispatchers.Default, riders, searchQuery)

        assertEquals(riders, searchResult)
    }
}
