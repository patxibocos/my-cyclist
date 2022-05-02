package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.data.RiderOfTeam
import io.github.patxibocos.mycyclist.ui.preview.riderPreview
import io.github.patxibocos.mycyclist.ui.preview.teamPreview

@Preview
@Composable
internal fun RiderScreen(
    riderOfTeam: RiderOfTeam = RiderOfTeam(riderPreview, teamPreview),
    onTeamSelected: (Team) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = riderOfTeam.rider.lastName)
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
            Text(text = riderOfTeam.rider.lastName)
            Text(
                text = riderOfTeam.team.name,
                modifier = Modifier.clickable {
                    onTeamSelected(riderOfTeam.team)
                }
            )
        }
    }
}
