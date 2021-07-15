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
) {
    companion object {
        val Preview by lazy {
            Rider(
                id = "patxi-bocos",
                firstName = "Patxi",
                lastName = "Bocos",
                country = "ES",
                website = "https://github.com/patxibocos/",
                birthDate = "1986-10-04",
                birthPlace = "Barakaldo",
                weight = 70,
                height = 185,
                photo = "https://avatars.githubusercontent.com/u/4415614",
            )
        }
    }
}