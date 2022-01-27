package io.github.patxibocos.roadcyclingdata.data

import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass.Race
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass.Team
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    val teams: Flow<List<Team>>
    val riders: Flow<List<Rider>>
    val races: Flow<List<Race>>
}
