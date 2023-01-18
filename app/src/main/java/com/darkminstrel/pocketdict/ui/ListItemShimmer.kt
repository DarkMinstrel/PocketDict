package com.darkminstrel.pocketdict.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkminstrel.pocketdict.ui.theme.PocketTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun ListItemShimmer(modifier: Modifier = Modifier) = Column(
    modifier = modifier
        .fillMaxWidth()
        .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
) {
    val color = MaterialTheme.colors.primary.copy(0.1f)
    Box(
        Modifier
            .clip(CircleShape)
            .background(color)
            .size(300.dp, 16.dp)
            .shimmer()
    )
    Box(
        Modifier
            .clip(CircleShape)
            .background(color)
            .size(250.dp, 12.dp)
            .shimmer()
    )
}

@Preview
@Composable
private fun PreviewListItemShimmer() = PocketTheme {
    ListItemShimmer()
}

