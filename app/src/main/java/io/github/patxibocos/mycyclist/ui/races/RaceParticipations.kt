package io.github.patxibocos.mycyclist.ui.races

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.data.Team
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar

@Composable
internal fun RaceParticipationsRoute(
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onBackPressed: () -> Unit = {},
    viewModel: RaceParticipationsViewModel = hiltViewModel(),
) {
    val raceParticipationsViewState by viewModel.raceParticipationsViewState.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    RaceParticipationsScreen(
        raceParticipationsViewState = raceParticipationsViewState,
        topBarState = topBarState,
        onRiderSelected = onRiderSelected,
        onTeamSelected = onTeamSelected,
        onBackPressed = onBackPressed,
        onSearched = viewModel::onSearched,
        onToggled = viewModel::onToggled,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    topBarState: TopBarState,
    focusManager: FocusManager,
    onBackPressed: () -> Unit = {},
    onSearched: (String) -> Unit,
    onToggled: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var showKeyboard by remember { mutableStateOf(false) }
    SmallTopAppBar(
        title = {
            AnimatedContent(topBarState.searching, label = "RidersTopAppBarAnimatedContent") {
                if (it) {
                    TextField(
                        value = topBarState.search,
                        onValueChange = { search ->
                            onSearched(search)
                        },
                        placeholder = {
                            Text(stringResource(R.string.riders_search))
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Words,
                            autoCorrect = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search,
                        ),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                        }),
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier.focusRequester(focusRequester),
                    )
                    if (showKeyboard) {
                        showKeyboard = false
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                } else {
                    LaunchedEffect(Unit) {
                        focusManager.clearFocus()
                    }
                    Text(text = "Participations")
                }
            }
        },
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                if (!topBarState.searching) {
                    showKeyboard = true
                }
                onToggled()
            }) {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RaceParticipationsScreen(
    raceParticipationsViewState: RaceParticipationsViewState,
    topBarState: TopBarState,
    onRiderSelected: (Rider) -> Unit,
    onTeamSelected: (Team) -> Unit,
    onBackPressed: () -> Unit = {},
    onSearched: (String) -> Unit,
    onToggled: () -> Unit,
) {
    Column {
        val focusManager = LocalFocusManager.current
        TopAppBar(
            topBarState = topBarState,
            focusManager = focusManager,
            onBackPressed = onBackPressed,
            onSearched = onSearched,
            onToggled = onToggled,
        )
        val lazyListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            state = lazyListState,
        ) {
            raceParticipationsViewState.ridersByTeam.forEach { (team, riderParticipations) ->
                stickyHeader {
                    Text(text = team.name, modifier = Modifier.clickable { onTeamSelected(team) })
                }
                items(
                    items = riderParticipations,
                    key = { it.rider.id },
                ) { (rider, number) ->
                    val riderText = if (number != 0) {
                        "${rider.fullName()} - $number"
                    } else {
                        rider.fullName()
                    }
                    Text(text = riderText, modifier = Modifier.clickable { onRiderSelected(rider) })
                }
            }
        }
    }
}
