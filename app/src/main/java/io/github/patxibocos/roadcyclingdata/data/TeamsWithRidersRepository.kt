package io.github.patxibocos.roadcyclingdata.data

import kotlinx.coroutines.flow.Flow

interface TeamsWithRidersRepository {
    fun teamsWithRiders(): Flow<TeamsWithRiders>
}