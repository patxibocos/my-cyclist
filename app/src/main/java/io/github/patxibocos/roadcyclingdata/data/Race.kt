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
    val startDate: LocalDate
)

@Immutable
@Stable
data class Stage(
    val id: String,
    val distance: Float,
    val startDate: LocalDate
)
