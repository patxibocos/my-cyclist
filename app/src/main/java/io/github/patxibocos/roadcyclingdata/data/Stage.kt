package io.github.patxibocos.roadcyclingdata.data

import java.time.LocalDate

data class Stage(
    val id: String,
    val startDate: LocalDate,
    val distance: Float,
    val departure: String,
    val arrival: String,
)
