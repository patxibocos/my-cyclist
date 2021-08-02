package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Race
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
internal data class JsonRace(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val website: String?,
    val stages: List<JsonStage>,
) {
    fun toRace(): Race = Race(
        id = id,
        name = name,
        startDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE),
        endDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE),
        website = website,
        stages = stages.map(JsonStage::toStage)
    )
}
