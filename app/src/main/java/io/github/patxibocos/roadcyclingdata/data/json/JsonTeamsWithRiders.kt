package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Rider
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
        status = status,
        abbreviation = abbreviation,
        country = country,
        bike = bike,
        jersey = jersey,
        website = website,
        year = year,
    )
}

@Serializable
internal data class JsonRider(
    val id: String,
    val firstName: String,
    val lastName: String,
    val country: String,
    val website: String?,
    val birthDate: String,
    val birthPlace: String?,
    val weight: Int?,
    val height: Int?,
    val photo: String,
) {
    fun toRider(team: Team): Rider = Rider(
        id = id,
        firstName = firstName,
        lastName = lastName,
        country = country,
        website = website,
        birthDate = birthDate,
        birthPlace = birthPlace,
        weight = weight,
        height = height,
        photo = photo,
        team = team,
    )
}
