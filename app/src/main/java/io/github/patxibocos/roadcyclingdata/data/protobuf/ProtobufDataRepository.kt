package io.github.patxibocos.roadcyclingdata.data.protobuf

import android.util.Base64
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass
import io.github.patxibocos.pcsscraper.protobuf.RacesOuterClass.Races
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass
import io.github.patxibocos.pcsscraper.protobuf.RidersOuterClass.Riders
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass
import io.github.patxibocos.pcsscraper.protobuf.TeamsOuterClass.Teams
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Stage
import io.github.patxibocos.roadcyclingdata.data.Status
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.time.Instant
import java.time.ZoneId
import java.util.zip.GZIPInputStream

internal class ProtobufDataRepository : DataRepository {

    private val _teams = MutableSharedFlow<List<Team>>(replay = 1)
    private val _riders = MutableSharedFlow<List<Rider>>(replay = 1)
    private val _races = MutableSharedFlow<List<Race>>(replay = 1)

    private fun decodeBase64ThenUnzip(gzipBase64: String) =
        ByteArrayInputStream(
            Base64.decode(
                gzipBase64,
                Base64.DEFAULT
            )
        ).use { inputStream -> GZIPInputStream(inputStream).use { it.readBytes() } }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3_600L
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.fetchAndActivate().await()

            val teamsBase64 = remoteConfig.getString("teams")
            val teams = Teams.parseFrom(decodeBase64ThenUnzip(teamsBase64))
            _teams.emit(teams.teamsList.map(TeamOuterClass.Team::toDomain))
            val ridersBase64 = remoteConfig.getString("riders")
            val riders = Riders.parseFrom(decodeBase64ThenUnzip(ridersBase64))
            _riders.emit(riders.ridersList.map(RiderOuterClass.Rider::toDomain))
            val racesBase64 = remoteConfig.getString("races")
            val races = Races.parseFrom(decodeBase64ThenUnzip(racesBase64))
            _races.emit(races.racesList.map(RaceOuterClass.Race::toDomain))
        }
    }

    override val teams = _teams
    override val riders = _riders
    override val races = _races
}

fun RaceOuterClass.Race.toDomain(): Race {
    return Race(
        id = this.id,
        name = this.name,
        country = this.country,
        startDate = Instant.ofEpochSecond(this.startDate.seconds).atZone(ZoneId.systemDefault())
            .toLocalDate(),
        stages = this.stagesList.map(RaceOuterClass.Stage::toDomain)
    )
}

fun RaceOuterClass.Stage.toDomain(): Stage {
    return Stage(
        id = this.id,
        distance = this.distance,
        startDate = Instant.ofEpochSecond(this.startDate.seconds).atZone(ZoneId.systemDefault())
            .toLocalDate()
    )
}

fun RiderOuterClass.Rider.toDomain(): Rider {
    return Rider(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        photo = this.photo,
        country = this.country
    )
}

fun TeamOuterClass.Team.toDomain(): Team {
    return Team(
        id = this.id,
        name = this.name,
        status = Status.PRO_TEAM,
        abbreviation = this.abbreviation,
        jersey = this.jersey,
        bike = this.bike,
        riderIds = this.riderIdsList
    )
}