package io.github.patxibocos.roadcyclingdata.ui.preview

import com.google.protobuf.Timestamp
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Race
import io.github.patxibocos.pcsscraper.protobuf.race.race
import io.github.patxibocos.pcsscraper.protobuf.race.riderParticipation
import io.github.patxibocos.pcsscraper.protobuf.race.stage
import io.github.patxibocos.pcsscraper.protobuf.race.teamParticipation
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider
import io.github.patxibocos.pcsscraper.protobuf.rider.rider
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team
import io.github.patxibocos.pcsscraper.protobuf.team.team

val teamPreview: Team by lazy {
    team {
        id = "cycling-team"
        name = "Cycling Team"
        status = Team.Status.STATUS_WORLD_TEAM
        abbreviation = "CT"
        country = "ES"
        bike = "Canyon"
        jersey = ""
        website = "https://github.com/patxibocos/"
        year = 2021
    }
}

val riderPreview: Rider by lazy {
    rider {
        id = "patxi-bocos"
        firstName = "Patxi"
        lastName = "Bocos"
        country = "ES"
        website = "https://github.com/patxibocos/"
        birthDate = Timestamp.getDefaultInstance()
        birthPlace = "Barakaldo"
        weight = 70
        height = 185
        photo = "https://avatars.githubusercontent.com/u/4415614"
    }
}

val racePreview: Race by lazy {
    race {
        id = "vuelta-a-espana"
        name = "La Vuelta ciclista a España"
        country = "ES"
        startDate = Timestamp.getDefaultInstance()
        endDate = Timestamp.getDefaultInstance()
        website = "https://www.lavuelta.es/"
        stages.addAll(
            listOf(
                stage {
                    id = "stage-1"
                    startDate = Timestamp.getDefaultInstance()
                    distance = 123F
                    type = RaceOuterClass.Stage.Type.TYPE_FLAT
                    departure = "Bilbao"
                    arrival = "Barcelona"
                },
                stage {
                    id = "stage-1"
                    startDate = Timestamp.getDefaultInstance()
                    distance = 200F
                    type = RaceOuterClass.Stage.Type.TYPE_HILLS_FLAT_FINISH
                    departure = "Barcelona"
                    arrival = "Madrid"
                }
            )
        )
        teams.addAll(
            listOf(
                teamParticipation {
                    teamId = teamPreview.id
                    riders.addAll(
                        listOf(
                            riderParticipation {
                                riderId = riderPreview.id
                                number = 1
                            }
                        )
                    )
                }
            )
        )
    }
}
