package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Stage
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
internal data class JsonStage(
    val id: String,
    @Contextual val startDate: LocalDate,
    val distance: Float,
    val type: String?,
    val departure: String?,
    val arrival: String?,
) {
    fun toStage(): Stage = Stage(
        id = id,
        startDate = startDate,
        distance = distance,
        type = type?.let { Stage.Type.valueOf(it) },
        departure = departure,
        arrival = arrival,
    )
}
