package io.github.patxibocos.roadcyclingdata.data

import kotlinx.coroutines.flow.Flow

interface DataRepository {
    val teams: Flow<List<Team>>
    val riders: Flow<List<Rider>>
    val races: Flow<List<Race>>
}
