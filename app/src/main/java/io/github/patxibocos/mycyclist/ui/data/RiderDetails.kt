package io.github.patxibocos.mycyclist.ui.data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.data.Team

@Immutable
@Stable
class RiderDetails(
    val rider: Rider,
    val team: Team,
    val participations: List<Participation>,
    val results: List<Result>,
)

@Immutable
@Stable
class Participation(val race: Race, val number: Int)
sealed class Result(open val position: Int) {
    class RaceResult(val race: Race, override val position: Int) : Result(position)
    class StageResult(
        val race: Race,
        val stage: Stage,
        val stageNumber: Int,
        override val position: Int
    ) : Result(position)
}
