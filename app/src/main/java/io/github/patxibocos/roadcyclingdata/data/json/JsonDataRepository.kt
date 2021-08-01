package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
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

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val (teamsJson, ridersJson, racesJson) = listOf(
                async(Dispatchers.IO) {
                    context.assets.open("teams.json").bufferedReader().use { it.readText() }
                },
                async(Dispatchers.IO) {
                    context.assets.open("riders.json").bufferedReader().use { it.readText() }
                },
                async(Dispatchers.IO) {
                    context.assets.open("races.json").bufferedReader().use { it.readText() }
                }
            ).awaitAll()
            val jsonTeams: List<JsonTeam> = Json.decodeFromString(teamsJson)
            val jsonRidersById: Map<String, JsonRider> =
                Json.decodeFromString<List<JsonRider>>(ridersJson).associateBy({ it.id }, { it })
            val teams = jsonTeams.map { jsonTeam ->
                jsonTeam.toTeam().also { team ->
                    val teamRiders: List<Rider> =
                        jsonTeam.riders.map(jsonRidersById::getValue).map { it.toRider(team) }
                    team.riders.addAll(teamRiders)
                }
            }
            val riders = getSortedRiders(teams)
            val races: List<Race> =
                Json.decodeFromString<List<JsonRace>>(racesJson).map(JsonRace::toRace)
            _teams.emit(teams)
            _riders.emit(riders)
            _races.emit(races)
        }
    }

    override fun teams(): Flow<List<Team>> = _teams
    override fun riders(): Flow<List<Rider>> = _riders
    override fun races(): Flow<List<Race>> = _races
}
