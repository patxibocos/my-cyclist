package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TeamsAndRidersRepository(private val context: Context) {

    private val _teamsAndRiders = MutableSharedFlow<TeamsAndRiders>(replay = 1)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val jsonContent =
                context.assets.open("2021.json").bufferedReader().use { it.readText() }
            val teamsAndRiders: TeamsAndRiders = Json.decodeFromString(jsonContent)
            _teamsAndRiders.emit(teamsAndRiders)
        }
    }

    fun teams(): Flow<List<Team>> {
        return _teamsAndRiders.map { it.teams }
    }

    fun riders(): Flow<List<Rider>> {
        return _teamsAndRiders.map { it.riders }
    }

}