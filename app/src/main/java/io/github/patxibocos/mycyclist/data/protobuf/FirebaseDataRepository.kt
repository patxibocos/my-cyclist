package io.github.patxibocos.mycyclist.data.protobuf

import android.util.Base64
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.github.patxibocos.mycyclist.data.DataRepository
import io.github.patxibocos.mycyclist.data.ParticipantResult
import io.github.patxibocos.mycyclist.data.ProfileType
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.RiderParticipation
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.StageType
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.TeamParticipation
import io.github.patxibocos.mycyclist.data.TeamStatus
import io.github.patxibocos.pcsscraper.protobuf.CyclingDataOuterClass.CyclingData
import io.github.patxibocos.pcsscraper.protobuf.RaceOuterClass
import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayInputStream
import java.time.Instant
import java.time.ZoneId
import java.util.zip.GZIPInputStream

internal class FirebaseDataRepository(
    defaultDispatcher: CoroutineDispatcher,
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) :
    DataRepository {

    companion object {
        private const val FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY = "cycling_data"
    }

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
        CoroutineScope(defaultDispatcher).launch {
            emitData(firebaseRemoteConfig.getString(FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY))
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3_600L
            }
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            try {
                if (firebaseRemoteConfig.fetchAndActivate().await()) {
                    emitData(firebaseRemoteConfig.getString(FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY))
                }
            } catch (e: FirebaseRemoteConfigException) {
                return@launch
            }
        }
    }

    private suspend fun emitData(serializedContent: String) {
        if (serializedContent.isNotEmpty()) {
            val cyclingData = CyclingData.parseFrom(decodeBase64ThenUnzip(serializedContent))
            _teams.emit(cyclingData.teamsList.map(TeamOuterClass.Team::toDomain))
            _riders.emit(cyclingData.ridersList.map(RiderOuterClass.Rider::toDomain))
            _races.emit(cyclingData.racesList.map(RaceOuterClass.Race::toDomain))
        }
    }


    override val teams = _teams
    override val riders = _riders
    override val races = _races

    override suspend fun refresh() {
        firebaseRemoteConfig.fetch(0).await()
        if (firebaseRemoteConfig.activate().await()) {
            emitData(firebaseRemoteConfig.getString(FIREBASE_REMOTE_CONFIG_CYCLING_DATA_KEY))
        }
    }
}

fun RaceOuterClass.Race.toDomain(): Race {
    return Race(
        id = this.id,
        name = this.name,
        country = this.country,
        startDate = Instant.ofEpochSecond(this.startDate.seconds).atZone(ZoneId.systemDefault())
            .toLocalDate(),
        endDate = Instant.ofEpochSecond(this.endDate.seconds).atZone(ZoneId.systemDefault())
            .toLocalDate(),
        stages = this.stagesList.map(RaceOuterClass.Stage::toDomain),
        website = this.website,
        teamParticipations = this.teamsList.map(RaceOuterClass.TeamParticipation::toDomain),
        result = this.resultList.map(RaceOuterClass.ParticipantResult::toDomain),
    )
}

fun RaceOuterClass.ParticipantResult.toDomain(): ParticipantResult {
    return ParticipantResult(
        position = this.position,
        participantId = this.participantId,
        time = this.time,
    )
}

fun RaceOuterClass.TeamParticipation.toDomain(): TeamParticipation {
    return TeamParticipation(
        teamId = this.teamId,
        riderParticipations = this.ridersList.map(RaceOuterClass.RiderParticipation::toDomain),
    )
}

fun RaceOuterClass.RiderParticipation.toDomain(): RiderParticipation {
    return RiderParticipation(
        riderId = this.riderId,
        number = this.number,
    )
}

fun RaceOuterClass.Stage.toDomain(): Stage {
    return Stage(
        id = this.id,
        distance = this.distance,
        startDateTime = Instant.ofEpochSecond(this.startDateTime.seconds)
            .atZone(ZoneId.systemDefault()),
        departure = this.departure,
        arrival = this.arrival,
        profileType = when (this.profileType) {
            RaceOuterClass.Stage.ProfileType.PROFILE_TYPE_FLAT -> ProfileType.FLAT
            RaceOuterClass.Stage.ProfileType.PROFILE_TYPE_HILLS_FLAT_FINISH -> ProfileType.HILLS_FLAT_FINISH
            RaceOuterClass.Stage.ProfileType.PROFILE_TYPE_HILLS_UPHILL_FINISH -> ProfileType.HILLS_UPHILL_FINISH
            RaceOuterClass.Stage.ProfileType.PROFILE_TYPE_MOUNTAINS_FLAT_FINISH -> ProfileType.MOUNTAINS_FLAT_FINISH
            RaceOuterClass.Stage.ProfileType.PROFILE_TYPE_MOUNTAINS_UPHILL_FINISH -> ProfileType.MOUNTAINS_UPHILL_FINISH
            else -> null
        },
        stageType = when (this.stageType) {
            RaceOuterClass.Stage.StageType.STAGE_TYPE_REGULAR -> StageType.REGULAR
            RaceOuterClass.Stage.StageType.STAGE_TYPE_INDIVIDUAL_TIME_TRIAL -> StageType.INDIVIDUAL_TIME_TRIAL
            RaceOuterClass.Stage.StageType.STAGE_TYPE_TEAM_TIME_TRIAL -> StageType.TEAM_TIME_TRIAL
            else -> StageType.REGULAR
        },
        result = this.resultList.map(RaceOuterClass.ParticipantResult::toDomain),
        gcResult = this.gcResultList.map(RaceOuterClass.ParticipantResult::toDomain),
    )
}

fun RiderOuterClass.Rider.toDomain(): Rider {
    return Rider(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        photo = this.photo,
        country = this.country,
        website = this.website,
        birthDate = Instant.ofEpochSecond(this.birthDate.seconds).atZone(ZoneId.systemDefault())
            .toLocalDate(),
        birthPlace = this.birthPlace,
        weight = this.weight,
        height = this.height,
        uciRankingPosition = this.uciRankingPosition,
    )
}

fun TeamOuterClass.Team.toDomain(): Team {
    return Team(
        id = this.id,
        name = this.name,
        status = when (this.status) {
            TeamOuterClass.Team.Status.STATUS_WORLD_TEAM -> TeamStatus.WORLD_TEAM
            TeamOuterClass.Team.Status.STATUS_PRO_TEAM -> TeamStatus.PRO_TEAM
            else -> error("Unexpected team status")
        },
        abbreviation = this.abbreviation,
        jersey = this.jersey,
        bike = this.bike,
        riderIds = this.riderIdsList,
        country = this.country,
        website = this.website
    )
}