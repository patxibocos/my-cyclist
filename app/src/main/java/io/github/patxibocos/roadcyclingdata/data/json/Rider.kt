package io.github.patxibocos.roadcyclingdata.data.json

import kotlinx.serialization.Serializable

@Serializable
data class Rider(
    val id: String,
    val firstName: String,
    val lastName: String,
    val country: String,
    val website: String?,
    val birthDate: String,
    val birthPlace: String?,
    val weight: Int?,
    val height: Int?,
    val photo: String,
)