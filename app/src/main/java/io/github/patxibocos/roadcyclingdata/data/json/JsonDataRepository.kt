package io.github.patxibocos.roadcyclingdata.data.json

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.RiderParticipation
import io.github.patxibocos.roadcyclingdata.data.RiderResult
import io.github.patxibocos.roadcyclingdata.data.Stage
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamParticipation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
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

internal class JsonDataRepository : DataRepository {

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

    @OptIn(ExperimentalSerializationApi::class)
    private suspend inline fun <reified T> readJson(jsonContent: String): List<T> =
        withContext(Dispatchers.Default) { json.decodeFromString(jsonContent) }

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
        val stagesById: Map<String, Stage> =
            races.flatMap(Race::stages).associateBy({ it.id }, { it })
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
            jsonRace.stages.forEach { jsonStage ->
                stagesById[jsonStage.id]!!.result.addAll(
                    jsonStage.result.mapNotNull { jsonRiderResult ->
                        ridersById[jsonRiderResult.rider]?.let { rider ->
                            RiderResult(
                                position = jsonRiderResult.position,
                                rider = rider,
                                time = jsonRiderResult.time,
                            )
                        }
                    }
                )
            }
        }
        return TeamsRacesRiders(teams, riders, races)
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = (3600 * 24).toLong()
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().await()

            val jsonTeamsDeferred = async { readJson<JsonTeam>(remoteConfig.getString("teams")) }
            val jsonRidersDeferred = async { readJson<JsonRider>(remoteConfig.getString("riders")) }
            val jsonRacesDeferred = async { readJson<JsonRace>(remoteConfig.getString("races")) }

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
