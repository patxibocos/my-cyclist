package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.Collator
import java.util.Locale

internal class JsonDataRepository(private val context: Context) :
    DataRepository {

    private val _teams = MutableSharedFlow<List<Team>>(replay = 1)
    private val _riders = MutableSharedFlow<List<Rider>>(replay = 1)
    private val _races = MutableSharedFlow<List<Race>>(replay = 1)

    private fun getSortedRiders(teams: List<Team>): List<Rider> {
        val usCollator = Collator.getInstance(Locale.US)
        val ridersComparator =
            compareBy(usCollator) { r: Rider -> r.lastName.lowercase() }.thenBy(usCollator) { r: Rider -> r.firstName.lowercase() }
        return teams.flatMap(Team::riders).distinctBy { it.id }.sortedWith(ridersComparator)
    }

    private suspend inline fun <T> readJson(fileName: String): List<T> {
        val teamsJson = withContext(Dispatchers.IO) {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
        return withContext(Dispatchers.Default) {
            Json.decodeFromString(teamsJson)
        }
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val jsonTeamsDeferred = async { readJson<JsonTeam>("teams.json") }
            val jsonRidersDeferred = async { readJson<JsonRider>("rides.json") }
            val jsonRacesDeferred = async { readJson<JsonRace>("races.json") }

            val jsonTeams = jsonTeamsDeferred.await()
            val jsonRiders = jsonRidersDeferred.await()
            val jsonRaces = jsonRacesDeferred.await()

            val jsonRidersById: Map<String, JsonRider> = jsonRiders.associateBy({ it.id }, { it })
            val teams = jsonTeams.map { jsonTeam ->
                jsonTeam.toTeam().also { team ->
                    val teamRiders: List<Rider> =
                        jsonTeam.riders.map(jsonRidersById::getValue).map { it.toRider(team) }
                    team.riders.addAll(teamRiders)
                }
            }
            val riders = getSortedRiders(teams)
            val races: List<Race> = jsonRaces.map(JsonRace::toRace)

            _teams.emit(teams)
            _riders.emit(riders)
            _races.emit(races)
        }
    }

    override fun teams(): Flow<List<Team>> = _teams
    override fun riders(): Flow<List<Rider>> = _riders
    override fun races(): Flow<List<Race>> = _races
}
