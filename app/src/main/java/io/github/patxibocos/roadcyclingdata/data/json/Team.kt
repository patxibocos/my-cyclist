package io.github.patxibocos.roadcyclingdata.data.json

import kotlinx.serialization.Serializable

@Serializable
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
    val riders: List<String>,
)