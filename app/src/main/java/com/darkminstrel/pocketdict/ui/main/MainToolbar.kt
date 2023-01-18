package com.darkminstrel.pocketdict.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.darkminstrel.pocketdict.SpeechState
import com.darkminstrel.pocketdict.ui.views.IconButton
import kotlinx.coroutines.delay
import com.darkminstrel.pocketdict.R

private val iconsVolume = arrayOf(
    R.drawable.ic_volume_0_24px,
    R.drawable.ic_volume_1_24px,
    R.drawable.ic_volume_2_24px
)

@Composable
internal fun MainToolbar(props: PropsMain) = TopAppBar(
    actions = {
        ToolbarPlayButton(props)
    },
    title = {
        if (props.state is ViewState.Data) {
            val parsed = props.state.parsed
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = parsed.source, style = MaterialTheme.typography.h6)
                if (!parsed.transcription.isNullOrEmpty()) Text(text = "[${parsed.transcription}]", style = MaterialTheme.typography.subtitle2)
            }
        } else {
            Text(text = stringResource(R.string.appName), style = MaterialTheme.typography.h5)
        }
    }
)

@Composable
private fun ToolbarPlayButton(props: PropsMain) {
    if (props.state is ViewState.Data) {
        val iconId = when (props.speechState) {
            SpeechState.IDLE -> iconsVolume.last()
            SpeechState.LOADING -> iconsVolume.first()
            SpeechState.UTTERING -> {
                var index by remember { mutableStateOf(0) }
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(200L); index = (index + 1) % iconsVolume.size
                    }
                }
                iconsVolume[index]
            }
        }
        IconButton(
            iconId = iconId,
            onClick = { props.onSpeak(props.state.parsed.source) },
            tint = MaterialTheme.colors.onPrimary,
        )
    }
}