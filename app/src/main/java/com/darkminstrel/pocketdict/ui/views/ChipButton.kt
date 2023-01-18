package com.darkminstrel.pocketdict.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkminstrel.pocketdict.ui.theme.PocketTheme

@Composable
fun ChipButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit = {},
) = Box(
    modifier = modifier
        .clip(CircleShape)
        .clickable(enabled = enabled, onClick = onClick)
        .background(if(selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
        .padding(top = 8.dp, bottom = 10.dp, start = 12.dp, end = 12.dp)
) {
    Text(
        text = text,
        style = MaterialTheme.typography.subtitle2,
        color = when {
            selected -> MaterialTheme.colors.onPrimary
            else -> MaterialTheme.colors.onSurface
        }.copy(alpha = if(enabled) 1f else 0.5f)


    )
}

@Preview
@Composable
private fun PreviewChipButton() = PocketTheme {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ChipButton(text = "On", selected = true)
        ChipButton(text = "Off", selected = false)
    }
}