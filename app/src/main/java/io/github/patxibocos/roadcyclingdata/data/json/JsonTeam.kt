package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.serialization.Serializable

@Serializable
internal data class JsonTeam(
    val id: String,
    val name: String,
    val status: String,
    val abbreviation: String,
    val country: String,
    val bike: String,
    val jersey: String,
    val website: String?,
    val year: Int,
    val riders: List<String>,
) {
    fun toTeam(): Team = Team(
        id = id,
        name = name,
        status = Team.Status.valueOf(status),
        abbreviation = abbreviation,
        country = country,
        bike = bike,
        jersey = jersey,
        website = website,
        year = year,
    )
}
