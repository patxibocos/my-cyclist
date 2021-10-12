package io.github.patxibocos.roadcyclingdata.data.protobuf

import android.util.Base64
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Race
import io.github.patxibocos.pcsscraper.protobuf.race.RacesOuterClass
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider
import io.github.patxibocos.pcsscraper.protobuf.rider.RidersOuterClass
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team
import io.github.patxibocos.pcsscraper.protobuf.team.TeamsOuterClass
import io.github.patxibocos.roadcyclingdata.data.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
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
            val teams = TeamsOuterClass.Teams.parseFrom(decodeBase64ThenUnzip(teamsBase64))
            _teams.emit(teams.teamsList)
            val ridersBase64 = remoteConfig.getString("riders")
            val riders = RidersOuterClass.Riders.parseFrom(decodeBase64ThenUnzip(ridersBase64))
            _riders.emit(riders.ridersList)
            val racesBase64 = remoteConfig.getString("races")
            val races = RacesOuterClass.Races.parseFrom(decodeBase64ThenUnzip(racesBase64))
            _races.emit(races.racesList)
        }
    }

    override fun teams(): Flow<List<Team>> = _teams
    override fun riders(): Flow<List<Rider>> = _riders
    override fun races(): Flow<List<Race>> = _races
}