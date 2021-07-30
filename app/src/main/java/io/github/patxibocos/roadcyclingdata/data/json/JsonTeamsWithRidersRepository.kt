package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamsAndRidersRepository
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

internal class JsonTeamsWithRidersRepository(private val context: Context) :
    TeamsAndRidersRepository {

    private val _teams = MutableSharedFlow<List<Team>>(replay = 1)
    private val _riders = MutableSharedFlow<List<Rider>>(replay = 1)

    private fun getSortedRiders(teams: List<Team>): List<Rider> {
        val usCollator = Collator.getInstance(Locale.US)
        val ridersComparator =
            compareBy(usCollator) { r: Rider -> r.lastName.lowercase() }.thenBy(usCollator) { r: Rider -> r.firstName.lowercase() }
        return teams.flatMap(Team::riders).distinctBy { it.id }.sortedWith(ridersComparator)
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val (teamsJson, ridersJson) = listOf(
                async(Dispatchers.IO) {
                    context.assets.open("teams.json").bufferedReader().use { it.readText() }
                },
                async(Dispatchers.IO) {
                    context.assets.open("riders.json").bufferedReader().use { it.readText() }
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
            _teams.emit(teams)
            _riders.emit(riders)
        }
    }

    override fun teams(): Flow<List<Team>> = _teams
    override fun riders(): Flow<List<Rider>> = _riders
}
