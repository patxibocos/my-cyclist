package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TeamsAndRidersRepository(private val context: Context) {

    private val _teamsAndRiders: Flow<TeamsAndRiders> = channelFlow {
        launch(Dispatchers.IO) {
            val jsonContent =
                context.assets.open("2021.json").bufferedReader().use { it.readText() }
            val teamsAndRiders: TeamsAndRiders = Json.decodeFromString(jsonContent)
            send(teamsAndRiders)
        }
    }

    fun teams(): Flow<List<Team>> {
        return _teamsAndRiders.map { it.teams }
    }

    fun riders(): Flow<List<Rider>> {
        return _teamsAndRiders.map { it.riders }
    }

}