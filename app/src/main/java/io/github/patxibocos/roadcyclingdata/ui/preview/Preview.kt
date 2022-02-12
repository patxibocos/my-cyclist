package io.github.patxibocos.roadcyclingdata.ui.preview

import io.github.patxibocos.roadcyclingdata.data.Race
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Stage
import io.github.patxibocos.roadcyclingdata.data.Status
import io.github.patxibocos.roadcyclingdata.data.Team
import java.time.LocalDate

val teamPreview: Team by lazy {
    Team(
        id = "cycling-team",
        name = "Cycling Team",
        status = Status.WORLD_TEAM,
        abbreviation = "CT",
        jersey = "",
        bike = "",
        riderIds = emptyList()
    )
}

val riderPreview: Rider by lazy {
    Rider(id = "patxi-bocos", firstName = "", lastName = "", photo = "", country = "")
}

val stagePreview: Stage by lazy {
    Stage(id = "stage-1", distance = 0f, startDate = LocalDate.now())
}

val racePreview: Race by lazy {
    Race(
        id = "vuelta-a-espana",
        name = "",
        stages = emptyList(),
        country = "",
        startDate = LocalDate.now()
    )
}
