package io.github.patxibocos.roadcyclingdata.data.protobuf

import android.util.Base64
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass
import io.github.patxibocos.pcsscraper.protobuf.race.RacesOuterClass
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass
import io.github.patxibocos.pcsscraper.protobuf.rider.RidersOuterClass
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass
import io.github.patxibocos.pcsscraper.protobuf.team.TeamsOuterClass
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
import java.text.Collator
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale

internal class ProtobufDataRepository : DataRepository {

    private val _teams = MutableSharedFlow<List<Team>>(replay = 1)
    private val _riders = MutableSharedFlow<List<Rider>>(replay = 1)
    private val _races = MutableSharedFlow<List<Race>>(replay = 1)

    private fun getSortedRiders(teams: List<Team>): List<Rider> {
        val usCollator = Collator.getInstance(Locale.US)
        val ridersComparator =
            compareBy(usCollator) { r: Rider -> r.lastName.lowercase() }.thenBy(usCollator) { r: Rider -> r.firstName.lowercase() }
        return teams.flatMap(Team::riders).distinctBy { it.id }.sortedWith(ridersComparator)
    }

    data class TeamsRacesRiders(
        val teams: List<Team>,
        val riders: List<Rider>,
        val races: List<Race>,
    )

    private fun TeamOuterClass.Team.toTeam() =
        Team(
            id = id,
            name = name,
            status = Team.Status.values()[status.number - 1],
            abbreviation = abbreviation,
            country = country,
            bike = bike,
            jersey = jersey,
            website = website,
            year = year,
        )

    private fun RiderOuterClass.Rider.toRider(team: Team) =
        Rider(
            id = id,
            firstName = firstName,
            lastName = lastName,
            country = country,
            website = website,
            birthDate = Instant.ofEpochSecond(birthDate.seconds).atZone(ZoneOffset.UTC)
                .toLocalDate(),
            birthPlace = birthPlace,
            weight = weight,
            height = height,
            photo = photo,
            team = team,
        )

    private fun RaceOuterClass.Race.toRace(): Race =
        Race(
            id = id,
            name = name,
            country = country,
            startDate = Instant.ofEpochSecond(startDate.seconds).atZone(ZoneOffset.UTC)
                .toLocalDate(),
            endDate = Instant.ofEpochSecond(endDate.seconds).atZone(ZoneOffset.UTC).toLocalDate(),
            website = website,
            stages = stagesList.map { it.toStage() },
        )

    private fun RaceOuterClass.Stage.toStage(): Stage =
        Stage(
            id = id,
            startDate = Instant.ofEpochSecond(startDate.seconds).atZone(ZoneOffset.UTC)
                .toLocalDate(),
            distance = distance,
            type = Stage.Type.values()[type.number - 1],
            departure = departure,
            arrival = arrival,
        )

    private fun buildTeamsRacesAndRiders(
        teamsMessage: TeamsOuterClass.Teams,
        ridersMessage: RidersOuterClass.Riders,
        racesMessage: RacesOuterClass.Races,
    ): TeamsRacesRiders {
        val jsonRidersById: Map<String, RiderOuterClass.Rider> =
            ridersMessage.ridersList.associateBy({ it.id }, { it })
        val teams = teamsMessage.teamsList.map { teamMessage ->
            teamMessage.toTeam().also { team ->
                val teamRiders: List<Rider> =
                    teamMessage.riderIdsList.map(jsonRidersById::getValue).map { it.toRider(team) }
                team.riders.addAll(teamRiders)
            }
        }
        val riders = getSortedRiders(teams)
        val races: List<Race> = racesMessage.racesList.map { it.toRace() }
        val teamsById: Map<String, Team> = teams.associateBy({ it.id }, { it })
        val ridersById: Map<String, Rider> = riders.associateBy({ it.id }, { it })
        val racesById: Map<String, Race> = races.associateBy({ it.id }, { it })
        val stagesById: Map<String, Stage> =
            races.flatMap(Race::stages).associateBy({ it.id }, { it })
        racesMessage.racesList.forEach { raceMessage ->
            racesById[raceMessage.id]!!.startList.addAll(
                raceMessage.teamsList.mapNotNull { jsonTeamParticipation ->
                    teamsById[jsonTeamParticipation.teamId]?.let { team ->
                        TeamParticipation(
                            team = team,
                            riders = jsonTeamParticipation.ridersList.map {
                                RiderParticipation(
                                    ridersById[it.riderId]!!,
                                    it.number
                                )
                            }
                        )
                    }
                }
            )
            raceMessage.stagesList.forEach { jsonStage ->
                stagesById[jsonStage.id]!!.result.addAll(
                    jsonStage.resultList.mapNotNull { jsonRiderResult ->
                        ridersById[jsonRiderResult.riderId]?.let { rider ->
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
                minimumFetchIntervalInSeconds = 3_600L
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().await()

            val jsonTeamsDeferred = async {
                TeamsOuterClass.Teams.parseFrom(
                    Base64.decode(remoteConfig.getString("teams"), Base64.DEFAULT)
                )
            }
            val jsonRidersDeferred = async {
                RidersOuterClass.Riders.parseFrom(
                    Base64.decode(remoteConfig.getString("riders"), Base64.DEFAULT)
                )
            }
            val jsonRacesDeferred = async {
                RacesOuterClass.Races.parseFrom(
                    Base64.decode(remoteConfig.getString("races"), Base64.DEFAULT)
                )
            }

            val teamsMessage = jsonTeamsDeferred.await()
            val ridersMessage = jsonRidersDeferred.await()
            val racesMessage = jsonRacesDeferred.await()

            val (teams, riders, races) = buildTeamsRacesAndRiders(
                teamsMessage,
                ridersMessage,
                racesMessage
            )

            _teams.emit(teams)
            _riders.emit(riders)
            _races.emit(races)
        }
    }

    override fun teams(): Flow<List<Team>> = _teams
    override fun riders(): Flow<List<Rider>> = _riders
    override fun races(): Flow<List<Race>> = _races
}
