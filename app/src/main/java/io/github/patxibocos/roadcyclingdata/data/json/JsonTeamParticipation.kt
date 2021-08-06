package io.github.patxibocos.roadcyclingdata.data.json

import kotlinx.serialization.Serializable

@Serializable
class JsonTeamParticipation(
    val team: String,
    val riders: List<String>,
)
