package io.github.patxibocos.roadcyclingdata.data

import java.time.LocalDate

data class Stage(
    val id: String,
    val startDate: LocalDate,
    val distance: Float,
    val type: Type?,
    val departure: String?,
    val arrival: String?,
) {
    enum class Type {
        FLAT,
        HILLS_FLAT_FINISH,
        HILLS_UPHILL_FINISH,
        MOUNTAINS_FLAT_FINISH,
        MOUNTAINS_UPHILL_FINISH,
    }
}
