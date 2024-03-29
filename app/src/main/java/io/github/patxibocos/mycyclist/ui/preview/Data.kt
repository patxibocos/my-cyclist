package io.github.patxibocos.mycyclist.ui.preview

import io.github.patxibocos.mycyclist.data.ProfileType
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.StageType
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.data.TeamStatus
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime

internal val teamPreview: Team by lazy {
    Team(
        id = "cycling-team",
        name = "Cycling Team",
        status = TeamStatus.WORLD_TEAM,
        abbreviation = "CT",
        country = "ES",
        bike = "Canyon",
        jersey = "",
        website = "https://github.com/patxibocos/",
        riderIds = emptyList(),
    )
}

internal val riderPreview: Rider by lazy {
    Rider(
        id = "patxi-bocos",
        firstName = "Patxi",
        lastName = "Bocos",
        country = "ES",
        website = "https://github.com/patxibocos/",
        birthDate = LocalDate.of(2020, Month.JANUARY, 31),
        birthPlace = "Barakaldo",
        weight = 70,
        height = 185,
        photo = "https://avatars.githubusercontent.com/u/4415614",
        uciRankingPosition = 1,
    )
}

private val stagePreview: Stage by lazy {
    Stage(
        id = "stage-1",
        distance = 123.4f,
        startDateTime = ZonedDateTime.of(2020, 1, 31, 13, 59, 59, 0, ZoneId.of("Europe/Madrid")),
        profileType = ProfileType.FLAT,
        departure = "Bilbao",
        arrival = "Barcelona",
        stageType = StageType.REGULAR,
        stageResults = io.github.patxibocos.mycyclist.data.StageResults(
            time = emptyList(),
            youth = emptyList(),
            teams = emptyList(),
            kom = emptyList(),
            points = emptyList(),
        ),
        generalResults = io.github.patxibocos.mycyclist.data.GeneralResults(
            time = emptyList(),
            youth = emptyList(),
            teams = emptyList(),
            kom = emptyList(),
            points = emptyList(),
        ),
    )
}

internal val racePreview: Race by lazy {
    Race(
        id = "vuelta-a-espana",
        name = "La Vuelta ciclista a España",
        country = "ES",
        website = "https://www.lavuelta.es/",
        stages = listOf(stagePreview),
        teamParticipations = emptyList(),
    )
}
