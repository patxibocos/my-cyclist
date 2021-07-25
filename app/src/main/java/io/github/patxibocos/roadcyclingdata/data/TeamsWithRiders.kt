package io.github.patxibocos.roadcyclingdata.data

import java.text.Collator
import java.util.Locale

class TeamsWithRiders(val teams: List<Team>) {

    val riders: List<Rider> get() = _riders

    private val usCollator = Collator.getInstance(Locale.US)
    private val ridersComparator =
        compareBy(usCollator) { r: Rider -> r.lastName.lowercase() }.thenBy(usCollator) { r: Rider -> r.firstName.lowercase() }
    private val _riders: List<Rider> =
        teams.flatMap(Team::riders).distinctBy { it.id }.sortedWith(ridersComparator)

}