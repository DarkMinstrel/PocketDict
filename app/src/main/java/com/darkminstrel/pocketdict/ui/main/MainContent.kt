@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)

package com.darkminstrel.pocketdict.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.darkminstrel.pocketdict.History
import com.darkminstrel.pocketdict.R
import com.darkminstrel.pocketdict.models.*
import com.darkminstrel.pocketdict.toTextFieldValueSelected
import com.darkminstrel.pocketdict.ui.ListItemShimmer
import com.darkminstrel.pocketdict.ui.ListItemTranslation
import com.darkminstrel.pocketdict.ui.views.ChipButton
import com.darkminstrel.pocketdict.ui.views.Icon
import kotlinx.coroutines.delay
import java.io.IOException

@Composable
internal fun MainContent(paddingValues: PaddingValues, props: PropsMain) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(props.state) {
        if (props.state is ViewState.Data) {
            delay(1000L)
            props.onAddToHistory(props.state.parsed.source, props.state.parsed.description)
        }
    }

    when (props.state) {
        is ViewState.History -> History(
            paddingValues = paddingValues,
            history = props.history,
            onDeleteFromHistory = props.onDeleteFromHistory,
            onItemClick = { key ->
                props.onQueryChange(key.toTextFieldValueSelected())
                keyboardController?.hide()
            },
            hideKeyboard = { keyboardController?.hide() }
        )
        ViewState.Loading -> Loading(paddingValues)
        is ViewState.Data -> Data(
            paddingValues = paddingValues,
            state = props.state,
            onChosenContext = props.onChosenContext,
            hideKeyboard = { keyboardController?.hide() }
        )
        is ViewState.Error -> Error(paddingValues, props.state)
    }
}

@Composable
private fun Loading(paddingValues: PaddingValues) = Column(Modifier.padding(paddingValues)) {
    val times = 6
    repeat(times) { ListItemShimmer(Modifier.alpha(1f - (it.toFloat() / times))) }
}

@Composable
private fun History(
    paddingValues: PaddingValues,
    history: History,
    onDeleteFromHistory: (String) -> Unit,
    onItemClick: (String) -> Unit,
    hideKeyboard: () -> Unit,
) {
    val listState = rememberLazyListState()
    EffectHideKeyboardOnScroll(listState, hideKeyboard)
    LazyColumn(
        state = listState,
        contentPadding = paddingValues
    ) {
        items(history.items.size, key = { index -> history.items[index].key }) { index ->
            val item = history.items[index]
            val dismissState = rememberDismissState()
            if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                onDeleteFromHistory(item.key)
            }
            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {}
            ) {
                ListItemTranslation(
                    modifier = Modifier.clickable { onItemClick(item.key) },
                    title = item.key,
                    subtitle = item.value,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun Data(
    paddingValues: PaddingValues,
    state: ViewState.Data,
    onChosenContext: (String) -> Unit,
    hideKeyboard: () -> Unit,
) {
    val listState = rememberLazyListState()
    EffectHideKeyboardOnScroll(listState, hideKeyboard)
    LazyColumn(
        state = listState,
        contentPadding = paddingValues
    ) {
        val parsed: ParsedTranslation = state.parsed
        val context: String? = state.context
        val pairs: List<TranslationPair> = parsed.getPairs(context)
        item {
            FlowRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                parsed.sortedItems.forEach { (key, value) ->
                    ChipButton(
                        modifier = Modifier.padding(bottom = 8.dp),
                        text = key,
                        enabled = value.isNotEmpty(),
                        selected = key == context,
                        onClick = {
                            hideKeyboard()
                            onChosenContext(key)
                        }
                    )
                }
            }
        }
        items(count = pairs.size, key = { index -> pairs[index].first }) { index ->
            ListItemTranslation(
                title = pairs[index].first,
                subtitle = pairs[index].second
            )
        }
    }
}

@Composable
private fun Error(paddingValues: PaddingValues, state: ViewState.Error) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Icon(
        iconId = R.drawable.ic_launcher_foreground,
        modifier = Modifier.requiredSize(120.dp),
        tint = MaterialTheme.colors.primary.copy(0.5f)
    )
    Text(
        text = when (state.error) {
            is ErrorTranslationHttp -> stringResource(R.string.errorHttp, state.error.httpCode)
            is ErrorTranslationServer -> state.error.serverMessage
            ErrorTranslationEmpty -> stringResource(R.string.errorEmpty)
            is IOException -> stringResource(R.string.errorNetwork)
            else -> stringResource(R.string.errorUnknown)
        },
        style = MaterialTheme.typography.h5,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun EffectHideKeyboardOnScroll(
    state: LazyListState,
    hideKeyboard: () -> Unit,
) {
    val currentHideKeyboard by rememberUpdatedState(hideKeyboard)
    LaunchedEffect(state.isScrollInProgress) {
        if (state.isScrollInProgress) currentHideKeyboard()
    }
}

