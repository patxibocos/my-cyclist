package io.github.patxibocos.roadcyclingdata.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Race(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val website: String?,
) {
    companion object {
        val Preview by lazy {
            Race(
                id = "vuelta-a-espana",
                name = "La Vuelta ciclista a España",
                startDate = LocalDate.parse("2021-08-14", DateTimeFormatter.ISO_LOCAL_DATE),
                endDate = LocalDate.parse("2021-09-05", DateTimeFormatter.ISO_LOCAL_DATE),
                website = "https://www.lavuelta.es/",
            )
        }
    }
}
