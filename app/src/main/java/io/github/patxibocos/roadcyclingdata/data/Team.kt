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
) {
    companion object {
        val Preview by lazy {
            Team(
                id = "cycling-team",
                name = "Cycling Team",
                status = "WT",
                abbreviation = "CT",
                country = "ES",
                bike = "Canyon",
                jersey = "",
                website = "https://github.com/patxibocos/",
                year = 2021,
                riders = mutableListOf(),
            )
        }
    }
}