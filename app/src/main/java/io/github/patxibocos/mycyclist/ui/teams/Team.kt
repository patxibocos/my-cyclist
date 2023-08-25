/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar

@Composable
internal fun TeamRoute(
    onRiderSelected: (Rider) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: TeamViewModel = hiltViewModel(),
) {
    val teamViewState by viewModel.teamViewState.collectAsState()
    TeamScreen(
        teamViewState = teamViewState,
        onRiderSelected = onRiderSelected,
        onBackPressed = onBackPressed,
    )
}

@Composable
internal fun TeamScreen(
    teamViewState: TeamViewState,
    onRiderSelected: (Rider) -> Unit,
    onBackPressed: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SmallTopAppBar(title = { Text(text = teamViewState.team?.name.toString()) }, onBackPressed)
        if (teamViewState.team != null) {
            Text(text = teamViewState.team.name)
            teamViewState.riders.forEach {
                RiderRow(rider = it, onRiderSelected = onRiderSelected)
            }
        }
    }
}

@Composable
private fun RiderRow(rider: Rider, onRiderSelected: (Rider) -> Unit) {
    Text(
        text = rider.lastName,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRiderSelected(rider) },
    )
}
