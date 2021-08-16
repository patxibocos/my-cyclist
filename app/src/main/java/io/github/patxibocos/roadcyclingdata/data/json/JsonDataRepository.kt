package io.github.patxibocos.roadcyclingdata.data.json

import android.content.Context
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.RiderParticipation
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamParticipation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.text.Collator
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal class JsonDataRepository(private val context: Context) :
    DataRepository {

    private val _teams = MutableSharedFlow<List<Team>>(replay = 1)
    private val _riders = MutableSharedFlow<List<Rider>>(replay = 1)
    private val _races = MutableSharedFlow<List<Race>>(replay = 1)

    private val json: Json = Json {
        serializersModule = SerializersModule {
            contextual(LocalDateSerializer)
        }
    }

    private object LocalDateSerializer : KSerializer<LocalDate> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("java.time.LocalDate", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: LocalDate) =
            encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE))

        override fun deserialize(decoder: Decoder): LocalDate =
            LocalDate.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun getSortedRiders(teams: List<Team>): List<Rider> {
        val usCollator = Collator.getInstance(Locale.US)
        val ridersComparator =
            compareBy(usCollator) { r: Rider -> r.lastName.lowercase() }.thenBy(usCollator) { r: Rider -> r.firstName.lowercase() }
        return teams.flatMap(Team::riders).distinctBy { it.id }.sortedWith(ridersComparator)
    }

    private suspend inline fun <reified T> readJson(fileName: String): List<T> {
        val teamsJson = withContext(Dispatchers.IO) {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        }
        return withContext(Dispatchers.Default) {
            json.decodeFromString(teamsJson)
        }
    }

    data class TeamsRacesRiders(
        val teams: List<Team>,
        val riders: List<Rider>,
        val races: List<Race>,
    )

    private fun buildTeamsRacesAndRiders(
        jsonTeams: List<JsonTeam>,
        jsonRiders: List<JsonRider>,
        jsonRaces: List<JsonRace>
    ): TeamsRacesRiders {
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
        val teamsById: Map<String, Team> = teams.associateBy({ it.id }, { it })
        val ridersById: Map<String, Rider> = riders.associateBy({ it.id }, { it })
        val racesById: Map<String, Race> = races.associateBy({ it.id }, { it })
        jsonRaces.forEach { jsonRace ->
            racesById[jsonRace.id]!!.startList.addAll(
                jsonRace.startList.mapNotNull { jsonTeamParticipation ->
                    teamsById[jsonTeamParticipation.team]?.let { team ->
                        TeamParticipation(
                            team = team,
                            riders = jsonTeamParticipation.riders.map {
                                RiderParticipation(
                                    ridersById[it.rider]!!,
                                    it.number
                                )
                            }
                        )
                    }
                }
            )
        }
        return TeamsRacesRiders(teams, riders, races)
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val jsonTeamsDeferred = async { readJson<JsonTeam>("teams.json") }
            val jsonRidersDeferred = async { readJson<JsonRider>("riders.json") }
            val jsonRacesDeferred = async { readJson<JsonRace>("races.json") }

            val jsonTeams = jsonTeamsDeferred.await()
            val jsonRiders = jsonRidersDeferred.await()
            val jsonRaces = jsonRacesDeferred.await()

            val (teams, riders, races) = buildTeamsRacesAndRiders(jsonTeams, jsonRiders, jsonRaces)

            _teams.emit(teams)
            _riders.emit(riders)
            _races.emit(races)
        }
    }

    override fun teams(): Flow<List<Team>> = _teams
    override fun riders(): Flow<List<Rider>> = _riders
    override fun races(): Flow<List<Race>> = _races
}
