package io.github.patxibocos.mycyclist.ui.data

import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.TeamStatus
import java.time.LocalDate

fun rider(
    id: String = "",
    firstName: String = "",
    lastName: String = "",
    photo: String = "",
    country: String = "",
    website: String = "",
    birthDate: LocalDate = LocalDate.now(),
    birthPlace: String = "",
    weight: Int = 0,
    height: Int = 0
): Rider =
    Rider(
        id = id,
        firstName = firstName,
        lastName = lastName,
        photo = photo,
        country = country,
        website = website,
        birthDate = birthDate,
        birthPlace = birthPlace,
        weight = weight,
        height = height
    )

fun team(
    id: String = "",
    name: String = "",
    status: TeamStatus = TeamStatus.WORLD_TEAM,
    abbreviation: String = "",
    country: String = "",
    bike: String = "",
    jersey: String = "",
    website: String = "",
    riderIds: List<String> = emptyList()
): Team =
    Team(
        id = id,
        name = name,
        status = status,
        abbreviation = abbreviation,
        country = country,
        bike = bike,
        jersey = jersey,
        website = website,
        riderIds = riderIds
    )
