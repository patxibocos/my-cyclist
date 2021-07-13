package io.github.patxibocos.roadcyclingdata.data.json

import kotlinx.serialization.Serializable

@Serializable
data class TeamsAndRiders(
    val season: Int,
    val teams: List<Team>,
    val riders: List<Rider>,
)

val Empty = TeamsAndRiders(0, emptyList(), emptyList())