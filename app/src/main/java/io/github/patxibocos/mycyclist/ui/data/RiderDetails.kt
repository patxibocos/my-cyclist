package io.github.patxibocos.mycyclist.ui.data

import androidx.compose.runtime.Immutable
import io.github.patxibocos.mycyclist.data.Race
import io.github.patxibocos.mycyclist.data.Stage

@Immutable
class Participation(val race: Race, val number: Int)

@Immutable
sealed class Result(open val race: Race, open val position: Int) {
    @Immutable
    class RaceResult(override val race: Race, override val position: Int) : Result(race, position)

    @Immutable
    class StageResult(
        override val race: Race,
        val stage: Stage,
        val stageNumber: Int,
        override val position: Int
    ) : Result(race, position)
}
