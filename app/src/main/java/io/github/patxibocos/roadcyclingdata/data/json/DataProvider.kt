package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DataProvider(private val context: Context) {

    private val _teamsAndRiders: MutableStateFlow<TeamsAndRiders> =
        MutableStateFlow(Empty)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonContent =
                context.assets.open("2021.json").bufferedReader().use { it.readText() }
            _teamsAndRiders.emit(Json.decodeFromString(jsonContent))
        }
    }

    fun teams(): Flow<List<Team>> {
        return _teamsAndRiders.map { it.teams }
    }

    fun riders(): Flow<List<Rider>> {
        return _teamsAndRiders.map { it.riders }
    }

}