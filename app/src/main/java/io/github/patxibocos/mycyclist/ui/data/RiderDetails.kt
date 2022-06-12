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
    val currentParticipation: Participation?,
    val pastParticipations: List<Participation>,
    val futureParticipations: List<Participation>,
    val results: List<Result>,
)

@Immutable
@Stable
class Participation(val race: Race, val number: Int)

@Immutable
@Stable
sealed class Result(open val race: Race, open val position: Int) {
    @Immutable
    @Stable
    class RaceResult(override val race: Race, override val position: Int) : Result(race, position)

    @Immutable
    @Stable
    class StageResult(
        override val race: Race,
        val stage: Stage,
        val stageNumber: Int,
        override val position: Int
    ) : Result(race, position)
}
