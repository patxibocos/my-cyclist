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
    val startDate: LocalDate,
    val endDate: LocalDate,
    val website: String,
    val teamParticipations: List<TeamParticipation>,
    val result: List<RiderResult>
)

@Immutable
data class RiderResult(val position: Int, val riderId: String, val time: Long)

@Immutable
data class TeamParticipation(val teamId: String, val riderParticipations: List<RiderParticipation>)

@Immutable
data class RiderParticipation(val riderId: String, val number: Int)

fun Race.isSingleDay(): Boolean =
    startDate == endDate

fun Race.isFinished(): Boolean =
    this.stages.last().result.isAvailable()

private fun today(): LocalDate = LocalDate.now(ZoneId.systemDefault())
fun Race.isActive(): Boolean = this.isPast().not() && this.isFuture().not()

fun Race.isPast(): Boolean = today().isAfter(this.endDate)

fun Race.isFuture(): Boolean = today().isBefore(this.startDate)

fun Race.todayStage(): Pair<Stage, Int>? =
    this.stages.find { it.startDateTime.toLocalDate() == today() }
        ?.let { it to this.stages.indexOf(it) }

@Immutable
data class Stage(
    val id: String,
    val distance: Float,
    val startDateTime: ZonedDateTime,
    val departure: String,
    val arrival: String,
    val type: StageType?,
    val timeTrial: Boolean,
    val result: List<RiderResult>
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
