package io.github.patxibocos.roadcyclingdata.data

import kotlinx.coroutines.flow.Flow

interface TeamsAndRidersRepository {
    fun teams(): Flow<List<Team>>
    fun riders(): Flow<List<Rider>>
}