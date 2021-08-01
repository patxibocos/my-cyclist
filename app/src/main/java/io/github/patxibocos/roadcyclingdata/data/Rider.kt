package io.github.patxibocos.roadcyclingdata.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Rider(
    val id: String,
    val firstName: String,
    val lastName: String,
    val country: String,
    val website: String?,
    val birthDate: LocalDate,
    val birthPlace: String?,
    val weight: Int?,
    val height: Int?,
    val photo: String,
    val team: Team,
) {
    companion object {
        val Preview by lazy {
            Rider(
                id = "patxi-bocos",
                firstName = "Patxi",
                lastName = "Bocos",
                country = "ES",
                website = "https://github.com/patxibocos/",
                birthDate = LocalDate.parse("1986-10-04", DateTimeFormatter.ISO_LOCAL_DATE),
                birthPlace = "Barakaldo",
                weight = 70,
                height = 185,
                photo = "https://avatars.githubusercontent.com/u/4415614",
                team = Team(
                    id = "movistar-team-2021",
                    name = "Movistar Team",
                    status = "WT",
                    abbreviation = "MOV",
                    country = "ES",
                    bike = "Canyon",
                    jersey = "https://www.procyclingstats.com/images/shirts/bx/eb/movistar-team-2021-n3.png",
                    website = "https://movistarteam.com/",
                    year = 2021,
                )
            )
        }
    }
}
