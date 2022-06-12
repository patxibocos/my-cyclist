package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.data.RiderDetails
import io.github.patxibocos.mycyclist.ui.preview.riderPreview
import io.github.patxibocos.mycyclist.ui.preview.teamPreview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun RiderScreen(
    riderDetails: RiderDetails = RiderDetails(
        riderPreview,
        teamPreview,
        null,
        emptyList(),
        emptyList(),
        emptyList()
    ),
    onTeamSelected: (Team) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    Scaffold(topBar = {
        SmallTopAppBar(
            title = {
                Text(text = riderDetails.rider.lastName)
            }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(text = riderDetails.rider.lastName)
            Text(
                text = riderDetails.team.name,
                modifier = Modifier.clickable {
                    onTeamSelected(riderDetails.team)
                }
            )
            riderDetails.currentParticipation?.let { currentParticipation ->
                Text(text = "Currently running ${currentParticipation.race.name}")
            }
        }
    }
}
