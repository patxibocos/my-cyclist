package io.github.patxibocos.mycyclist.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Immutable
@Stable
data class Race(
    val id: String,
    val name: String,
    val stages: List<Stage>,
    val country: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val website: String,
    val teamParticipations: List<TeamParticipation>,
    val result: List<RiderResult>,
)

data class RiderResult(val position: Int, val riderId: String, val time: Long)

@Immutable
@Stable
data class TeamParticipation(val teamId: String, val riderParticipations: List<RiderParticipation>)

@Immutable
@Stable
data class RiderParticipation(val riderId: String, val number: Int)

fun Race.isSingleDay(): Boolean =
    startDate == endDate

fun Race.getMoment(): RaceMoment {
    val today = LocalDate.now(ZoneId.systemDefault())
    return when {
        today.isAfter(endDate) -> RaceMoment.Past
        today.isBefore(startDate) -> RaceMoment.Future
        else -> RaceMoment.Active
    }
}

enum class RaceMoment {
    Past,
    Active,
    Future
}

@Immutable
@Stable
data class Stage(
    val id: String,
    val distance: Float,
    val startDateTime: ZonedDateTime,
    val departure: String,
    val arrival: String,
    val type: StageType?,
    val timeTrial: Boolean,
    val result: List<RiderResult>,
)

enum class StageType {
    FLAT,
    HILLS_FLAT_FINISH,
    HILLS_UPHILL_FINISH,
    MOUNTAINS_FLAT_FINISH,
    MOUNTAINS_UPHILL_FINISH
}

fun List<RiderResult>.isAvailable(): Boolean {
    return this.isNotEmpty()
}
