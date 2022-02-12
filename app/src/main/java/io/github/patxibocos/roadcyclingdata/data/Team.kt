package io.github.patxibocos.roadcyclingdata.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@Stable
data class Team(
    val id: String,
    val name: String,
    val status: Status,
    val abbreviation: String,
    val jersey: String,
    val bike: String,
    val riderIds: List<String>,
)

enum class Status {
    WORLD_TEAM,
    PRO_TEAM
}
