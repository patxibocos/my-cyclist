package io.github.patxibocos.roadcyclingdata.ui.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.patxibocos.roadcyclingdata.data.Rider
import io.github.patxibocos.roadcyclingdata.data.Team

@Immutable
@Stable
class TeamOfRiders(val team: Team, val riders: List<Rider>)
