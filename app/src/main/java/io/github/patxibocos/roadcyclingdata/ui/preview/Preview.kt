package io.github.patxibocos.roadcyclingdata.ui.preview

import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Stage
import io.github.patxibocos.roadcyclingdata.data.StageType
import io.github.patxibocos.roadcyclingdata.data.Team
import io.github.patxibocos.roadcyclingdata.data.TeamStatus
import java.time.LocalDate
import java.time.ZonedDateTime

val teamPreview: Team by lazy {
    Team(
        id = "cycling-team",
        name = "Cycling Team",
        status = TeamStatus.WORLD_TEAM,
        abbreviation = "CT",
        country = "ES",
        bike = "Canyon",
        jersey = "",
        website = "https://github.com/patxibocos/",
        riderIds = emptyList()
    )
}

val riderPreview: Rider by lazy {
    Rider(
        id = "patxi-bocos",
        firstName = "Patxi",
        lastName = "Bocos",
        country = "ES",
        website = "https://github.com/patxibocos/",
        birthDate = LocalDate.now(),
        birthPlace = "Barakaldo",
        weight = 70,
        height = 185,
        photo = "https://avatars.githubusercontent.com/u/4415614",
    )
}

val stagePreview: Stage by lazy {
    Stage(
        id = "stage-1",
        distance = 123.4f,
        startDateTime = ZonedDateTime.now(),
        type = StageType.FLAT,
        departure = "Bilbao",
        arrival = "Barcelona",
        timeTrial = false
    )
}

val racePreview: Race by lazy {
    Race(
        id = "vuelta-a-espana",
        name = "La Vuelta ciclista a España",
        country = "ES",
        startDate = LocalDate.now(),
        endDate = LocalDate.now(),
        website = "https://www.lavuelta.es/",
        stages = listOf(stagePreview),
    )
}
