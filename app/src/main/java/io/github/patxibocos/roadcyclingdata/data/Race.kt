package io.github.patxibocos.roadcyclingdata.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import java.time.LocalDate

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
)

@Immutable
@Stable
data class Stage(
    val id: String,
    val distance: Float,
    val startDate: LocalDate,
    val departure: String,
    val arrival: String,
    val type: StageType?,
)

enum class StageType {
    FLAT,
    HILLS_FLAT_FINISH,
    HILLS_UPHILL_FINISH,
    MOUNTAINS_FLAT_FINISH,
    MOUNTAINS_UPHILL_FINISH
}
