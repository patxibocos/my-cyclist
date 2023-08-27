/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.riders

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.R
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.ui.home.Screen
import io.github.patxibocos.mycyclist.ui.util.RefreshableContent
import io.github.patxibocos.mycyclist.ui.util.RemoteImage
import io.github.patxibocos.mycyclist.ui.util.getCountryEmoji
import kotlinx.coroutines.launch

@Composable
internal fun RidersRoute(
    onRiderSelected: (Rider) -> Unit,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
    viewModel: RidersViewModel = hiltViewModel(),
) {
    val ridersViewState by viewModel.ridersState.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    RidersScreen(
        ridersViewState = ridersViewState,
        topBarState = topBarState,
        onRiderSearched = viewModel::onSearched,
        onRiderSelected = onRiderSelected,
        onSortingSelected = viewModel::onSorted,
        reselectedScreen = reselectedScreen,
        onReselectedScreenConsumed = onReselectedScreenConsumed,
        onToggled = viewModel::onToggled,
        onRefreshed = viewModel::onRefreshed,
    )
}

@Composable
private fun RidersScreen(
    ridersViewState: RidersViewState,
    topBarState: TopBarState,
    onRiderSearched: (String) -> Unit,
    onRiderSelected: (Rider) -> Unit,
    onSortingSelected: (Sorting) -> Unit,
    reselectedScreen: State<Screen?>,
    onReselectedScreenConsumed: () -> Unit,
    onToggled: () -> Unit,
    onRefreshed: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    Column {
        TopAppBar(
            topBarState,
            focusManager,
            onSortingSelected,
            onRiderSearched,
            onToggled = onToggled,
            onClicked = {
                lazyListState.scrollToItem(0)
            },
        )
        Surface {
            RidersList(
                ridersState = ridersViewState,
                onRiderSelected = {
                    focusManager.clearFocus()
                    onRiderSelected(it)
                },
                screenReselected = reselectedScreen,
                lazyListState = lazyListState,
                onReselectedScreenConsumed = onReselectedScreenConsumed,
                onRefreshed = onRefreshed,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    topBarState: TopBarState,
    focusManager: FocusManager,
    onSortingSelected: (Sorting) -> Unit,
    onSearched: (String) -> Unit,
    onToggled: () -> Unit,
    onClicked: suspend () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var showKeyboard by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue(topBarState.search)) }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
        title = {
            val interactionSource = remember { MutableInteractionSource() }
            val coroutineScope = rememberCoroutineScope()
            AnimatedContent(topBarState.searching, label = "RidersTopAppBarAnimatedContent") {
                if (it) {
                    TextField(
                        value = searchText,
                        onValueChange = { search ->
                            searchText = search
                            onSearched(search.text)
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
                    Text(
                        text = stringResource(R.string.riders_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) {
                                coroutineScope.launch {
                                    onClicked()
                                }
                            }
                            .fillMaxWidth(),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                if (!topBarState.searching) {
                    showKeyboard = true
                } else {
                    searchText = TextFieldValue("")
                }
                onToggled()
            }) {
                val icon =
                    if (topBarState.searching) Icons.Outlined.Close else Icons.Outlined.Search
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        actions = {
            Box {
                var sortingOptionsVisible by remember { mutableStateOf(false) }
                IconButton(onClick = { sortingOptionsVisible = true }) {
                    Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
                }
                SortingMenu(
                    expanded = sortingOptionsVisible,
                    selectedSorting = topBarState.sorting,
                    onSortingSelected = { sorting ->
                        sortingOptionsVisible = false
                        onSortingSelected(sorting)
                    },
                    onDismissed = { sortingOptionsVisible = false },
                )
            }
        },
    )
}

@Composable
private fun SortingMenu(
    expanded: Boolean,
    selectedSorting: Sorting,
    onSortingSelected: (Sorting) -> Unit,
    onDismissed: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissed,
    ) {
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.UciRanking)
            },
            enabled = selectedSorting != Sorting.UciRanking,
            text = {
                Text(stringResource(R.string.riders_sort_uci_ranking))
            },
        )
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.LastName)
            },
            enabled = selectedSorting != Sorting.LastName,
            text = {
                Text(stringResource(R.string.riders_sort_name))
            },
        )
        DropdownMenuItem(
            onClick = {
                onSortingSelected(Sorting.Country)
            },
            enabled = selectedSorting != Sorting.Country,
            text = {
                Text(stringResource(R.string.riders_sort_country))
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RidersList(
    ridersState: RidersViewState,
    onRiderSelected: (Rider) -> Unit,
    screenReselected: State<Screen?>,
    lazyListState: LazyListState,
    onReselectedScreenConsumed: () -> Unit,
    onRefreshed: () -> Unit,
) {
    LaunchedEffect(key1 = screenReselected.value) {
        if (screenReselected.value == Screen.Riders) {
            lazyListState.scrollToItem(0)
            onReselectedScreenConsumed()
        }
    }
    RefreshableContent(isRefreshing = ridersState.isRefreshing, onRefreshed = onRefreshed) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            state = lazyListState,
        ) {
            when (ridersState.riders) {
                is RidersViewState.Riders.ByLastName -> {
                    ridersState.riders.riders.forEach { (letter, riders) ->
                        stickyHeader {
                            Text(text = letter.toString())
                        }
                        items(riders, key = Rider::id) { rider ->
                            RiderRow(rider, onRiderSelected)
                        }
                    }
                }

                is RidersViewState.Riders.ByCountry -> {
                    ridersState.riders.riders.forEach { (country, riders) ->
                        stickyHeader {
                            Text(text = country)
                        }
                        items(riders, key = Rider::id) { rider ->
                            RiderRow(rider, onRiderSelected)
                        }
                    }
                }

                is RidersViewState.Riders.ByUciRanking -> {
                    items(ridersState.riders.riders, key = Rider::id) { rider ->
                        RiderRow(rider, onRiderSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun RiderRow(
    rider: Rider,
    onRiderSelected: (Rider) -> Unit,
) {
    Column(modifier = Modifier.clickable { onRiderSelected(rider) }) {
        Row(modifier = Modifier.fillMaxWidth()) {
            RemoteImage(
                url = rider.photo,
                modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .padding(2.dp)
                    .size(75.dp)
                    .clip(CircleShape),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
            ) {
                Text(
                    text = "${rider.lastName.uppercase()} ${rider.firstName}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Country(countryCode = rider.country, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}

@Composable
private fun Country(countryCode: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "${getCountryEmoji(countryCode)} $countryCode",
        style = MaterialTheme.typography.bodyLarge,
    )
}
