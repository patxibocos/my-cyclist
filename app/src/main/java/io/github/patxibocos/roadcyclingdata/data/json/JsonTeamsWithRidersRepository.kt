package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import io.github.patxibocos.roadcyclingdata.data.TeamsWithRiders
import io.github.patxibocos.roadcyclingdata.data.TeamsWithRidersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal class JsonTeamsWithRidersRepository(private val context: Context) :
    TeamsWithRidersRepository {

    private val _teamsWithRiders = MutableSharedFlow<TeamsWithRiders>(replay = 1)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val jsonContent =
                context.assets.open("2021.json").bufferedReader().use { it.readText() }
            val jsonJsonTeamsWithRiders: JsonTeamsWithRiders = Json.decodeFromString(jsonContent)
            val teamsWithRiders =
                TeamsWithRiders(teams = jsonJsonTeamsWithRiders.teams.map { jsonTeam ->
                    jsonTeam.toTeam().also { team ->
                        team.riders.addAll(jsonTeam.riders.map { jsonRider ->
                            jsonRider.toRider(team)
                        })
                    }
                })
            _teamsWithRiders.emit(teamsWithRiders)
        }
    }

    override fun teamsWithRiders(): Flow<TeamsWithRiders> = _teamsWithRiders

}