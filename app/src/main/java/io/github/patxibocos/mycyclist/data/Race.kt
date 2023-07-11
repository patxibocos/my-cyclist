package io.github.patxibocos.mycyclist.data

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Immutable
data class Race(
    val id: String,
    val name: String,
    val stages: List<Stage>,
    val country: String,
    val website: String,
    val teamParticipations: List<TeamParticipation>,
)

@Immutable
data class ParticipantResultTime(val position: Int, val participantId: String, val time: Long)

@Immutable
data class ParticipantResultPoints(val position: Int, val participant: String, val points: Int)

@Immutable
data class PlaceResult(val place: Place, val points: List<ParticipantResultPoints>)

@Immutable
data class Place(val name: String, val distance: Float)

@Immutable
data class TeamParticipation(val teamId: String, val riderParticipations: List<RiderParticipation>)

@Immutable
data class RiderParticipation(val riderId: String, val number: Int)

fun Race.isSingleDay(): Boolean = this.stages.size == 1

fun Race.startDate(): LocalDate =
    this.stages.first().startDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()

fun Race.endDate(): LocalDate =
    this.stages.last().startDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate()

private fun today(): LocalDate = LocalDate.now(ZoneId.systemDefault())

fun Race.isActive(): Boolean = this.isPast().not() && this.isFuture().not()

fun Race.isPast(): Boolean = today().isAfter(this.endDate())

fun Race.isFuture(): Boolean = today().isBefore(this.startDate())

fun Race.todayStage(): Pair<Stage, Int>? =
    this.stages.find { it.startDateTime.toLocalDate() == today() }
        ?.let { it to this.stages.indexOf(it) }

fun Race.indexOfLastStageWithResults(): Int =
    this.stages.indexOfLast { it.stageResults.time.isNotEmpty() }

fun Race.result(): List<ParticipantResultTime>? =
    this.stages.last().generalResults.time.takeIf { it.isAvailable() }

@Immutable
data class Stage(
    val id: String,
    val distance: Float,
    val startDateTime: ZonedDateTime,
    val departure: String,
    val arrival: String,
    val profileType: ProfileType?,
    val stageType: StageType,
    val stageResults: StageResults,
    val generalResults: GeneralResults,
)

@Immutable
data class StageResults(
    val time: List<ParticipantResultTime>,
    val youth: List<ParticipantResultTime>,
    val teams: List<ParticipantResultTime>,
    val kom: List<PlaceResult>,
    val points: List<PlaceResult>,
)

@Immutable
data class GeneralResults(
    val time: List<ParticipantResultTime>,
    val youth: List<ParticipantResultTime>,
    val teams: List<ParticipantResultTime>,
    val kom: List<ParticipantResultPoints>,
    val points: List<ParticipantResultPoints>,
)

fun Stage.areResultsAvailable() = this.stageResults.time.isNotEmpty()

enum class ProfileType {
    FLAT,
    HILLS_FLAT_FINISH,
    HILLS_UPHILL_FINISH,
    MOUNTAINS_FLAT_FINISH,
    MOUNTAINS_UPHILL_FINISH,
}

enum class StageType {
    REGULAR,
    INDIVIDUAL_TIME_TRIAL,
    TEAM_TIME_TRIAL,
}

fun List<ParticipantResultTime>.isAvailable(): Boolean {
    return this.isNotEmpty()
}
