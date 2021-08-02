package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Stage
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
internal data class JsonStage(
    val id: String,
    val startDate: String,
) {
    fun toStage(): Stage = Stage(
        id = id,
        startDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
    )
}
