package io.github.patxibocos.roadcyclingdata.data

data class Team(
    val id: String,
    val name: String,
    val status: String,
    val abbreviation: String,
    val country: String,
    val bike: String,
    val jersey: String,
    val website: String?,
    val year: Int,
    val riders: MutableList<Rider> = mutableListOf(),
)