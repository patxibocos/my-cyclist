package io.github.patxibocos.roadcyclingdata.data

import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun teams(): Flow<List<TeamOuterClass.Team>>
    fun riders(): Flow<List<RiderOuterClass.Rider>>
    fun races(): Flow<List<RaceOuterClass.Race>>
}
