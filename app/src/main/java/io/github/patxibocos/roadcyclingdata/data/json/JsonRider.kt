package io.github.patxibocos.roadcyclingdata.data.json

import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
internal data class JsonRider(
    val id: String,
    val firstName: String,
    val lastName: String,
    val country: String,
    val website: String?,
    @Contextual val birthDate: LocalDate,
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
