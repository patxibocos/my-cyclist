package io.github.patxibocos.roadcyclingdata.data

import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun teams(): Flow<List<Team>>
    fun riders(): Flow<List<Rider>>
    fun races(): Flow<List<Race>>
}
