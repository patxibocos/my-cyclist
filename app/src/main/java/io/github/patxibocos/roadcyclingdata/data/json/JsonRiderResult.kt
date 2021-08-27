package io.github.patxibocos.roadcyclingdata.data.json

import kotlinx.serialization.Serializable

@Serializable
class JsonRiderResult(
    val position: Int,
    val rider: String,
    val time: Long,
)
