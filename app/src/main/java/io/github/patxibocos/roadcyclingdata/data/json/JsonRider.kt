package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        birthDate = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE),
        birthPlace = birthPlace,
        weight = weight,
        height = height,
        photo = photo,
        team = team,
    )
}
