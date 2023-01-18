@file:OptIn(ExperimentalComposeUiApi::class)

package com.darkminstrel.pocketdict.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.darkminstrel.pocketdict.models.demo
import com.darkminstrel.pocketdict.toTextFieldValueSelected
import com.darkminstrel.pocketdict.ui.theme.PocketTheme

@Composable
fun ScreenMain(
    props: PropsMain,
) {
    EffectOnResume {
        props.onQueryChange(props.query.text.toTextFieldValueSelected())
    }
    BackHandler(enabled = props.query.text.isNotEmpty()) {
        props.onQueryChange(TextFieldValue())
    }

    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .background(MaterialTheme.colors.background),
        topBar = {
            MainToolbar(props = props)
        },
        content = {
            MainContent(paddingValues = it, props = props)
        },
        bottomBar = {
            MainBottomBar(
                modifier = Modifier.imePadding(),
                query = props.query,
                onQueryChange = props.onQueryChange
            )
        },
    )
}

@Composable
private fun EffectOnResume(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onResume: () -> Unit,
) {
    val currentOnResume by rememberUpdatedState(onResume)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) currentOnResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Preview
@Composable
private fun PreviewScreenMain_Data() = PocketTheme {
    ScreenMain(PropsMain(state = ViewState.Data(demo, demo.defaultContext)))
}

@Preview
@Composable
private fun PreviewScreenMain_Loading() = PocketTheme {
    ScreenMain(PropsMain(state = ViewState.Loading))
}