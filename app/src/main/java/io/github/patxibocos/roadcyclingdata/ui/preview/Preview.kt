package io.github.patxibocos.roadcyclingdata.ui.preview

import com.google.protobuf.Timestamp
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Race
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.RiderParticipation
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Stage
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.TeamParticipation
import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team

val teamPreview: Team by lazy {
    Team.newBuilder()
        .setId("cycling-team")
        .setName("Cycling Team")
        .setStatus(Team.Status.STATUS_WORLD_TEAM)
        .setAbbreviation("CT")
        .setCountry("ES")
        .setBike("Canyon")
        .setJersey("")
        .setWebsite("https://github.com/patxibocos/")
        .setYear(2021)
        .build()
}

val riderPreview: Rider by lazy {
    Rider.newBuilder()
        .setId("patxi-bocos")
        .setFirstName("Patxi")
        .setLastName("Bocos")
        .setCountry("ES")
        .setWebsite("https://github.com/patxibocos/")
        .setBirthDate(Timestamp.getDefaultInstance())
        .setBirthPlace("Barakaldo")
        .setWeight(70)
        .setHeight(185)
        .setPhoto("https://avatars.githubusercontent.com/u/4415614")
        .build()
}

val stagePreview: Stage by lazy {
    Stage.newBuilder()
        .setId("stage-1")
        .setStartDate(Timestamp.getDefaultInstance())
        .setDistance(123F)
        .setType(Stage.Type.TYPE_FLAT)
        .setDeparture("Bilbao")
        .setArrival("Barcelona")
        .build()
}

val racePreview: Race by lazy {
    Race.newBuilder()
        .setId("vuelta-a-espana")
        .setName("La Vuelta ciclista a España")
        .setCountry("ES")
        .setStartDate(Timestamp.getDefaultInstance())
        .setEndDate(Timestamp.getDefaultInstance())
        .setWebsite("https://www.lavuelta.es/")
        .addAllStages(
            listOf(stagePreview)
        )
        .addAllTeams(
            listOf(
                TeamParticipation.newBuilder()
                    .setTeamId(teamPreview.id)
                    .addAllRiders(
                        listOf(
                            RiderParticipation.newBuilder()
                                .setRiderId(riderPreview.id)
                                .setNumber(1)
                                .build()
                        )
                    )
                    .build()
            )
        )
        .build()
}
