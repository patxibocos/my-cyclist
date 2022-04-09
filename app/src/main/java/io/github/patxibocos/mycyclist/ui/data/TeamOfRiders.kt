package io.github.patxibocos.mycyclist.ui.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team

@Immutable
@Stable
class TeamOfRiders(val team: Team, val riders: List<Rider>)
