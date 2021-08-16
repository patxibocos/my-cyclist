package io.github.patxibocos.roadcyclingdata.data.json

import kotlinx.serialization.Serializable

@Serializable
class JsonRiderParticipation(
    val rider: String,
    val number: Int?,
)
