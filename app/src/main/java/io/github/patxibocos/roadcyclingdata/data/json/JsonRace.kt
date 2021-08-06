package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Race
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
internal data class JsonRace(
    val id: String,
    val name: String,
    @Contextual val startDate: LocalDate,
    @Contextual val endDate: LocalDate,
    val website: String?,
    val stages: List<JsonStage>,
    val startList: List<JsonTeamParticipation>,
) {
    fun toRace(): Race = Race(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        website = website,
        stages = stages.map(JsonStage::toStage),
    )
}
