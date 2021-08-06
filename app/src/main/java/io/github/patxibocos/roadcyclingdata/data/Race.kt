package io.github.patxibocos.roadcyclingdata.data

import java.time.LocalDate

data class Race(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val website: String?,
    val stages: List<Stage>,
    val startList: MutableList<TeamParticipation> = mutableListOf(),
) {
    companion object {
        val Preview by lazy {
            Race(
                id = "vuelta-a-espana",
                name = "La Vuelta ciclista a España",
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                website = "https://www.lavuelta.es/",
                stages = listOf(
                    Stage(
                        id = "stage-1",
                        startDate = LocalDate.now(),
                        distance = 123F,
                        departure = "Bilbao",
                        arrival = "Barcelona",
                    ),
                    Stage(
                        id = "stage-1",
                        startDate = LocalDate.now(),
                        distance = 200F,
                        departure = "Barcelona",
                        arrival = "Madrid",
                    )
                ),
                startList = mutableListOf(
                    TeamParticipation(
                        team = Team.Preview,
                        riders = listOf(
                            Rider.Preview
                        ),
                    )
                )
            )
        }
    }
}
