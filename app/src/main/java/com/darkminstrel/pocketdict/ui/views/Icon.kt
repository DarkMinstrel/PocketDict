package com.darkminstrel.pocketdict.ui.views

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@Composable
fun Icon(
    @DrawableRes iconId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) = androidx.compose.material.Icon(
    painter = painterResource(id = iconId),
    contentDescription = contentDescription,
    modifier = modifier,
    tint = tint,
)

@Composable
fun IconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) = androidx.compose.material.IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    interactionSource = interactionSource,
) {
    androidx.compose.material.Icon(imageVector = imageVector, tint = tint, contentDescription = null)
}

@Composable
fun IconButton(
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) = androidx.compose.material.IconButton(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    interactionSource = interactionSource,
) {
    Icon(iconId = iconId, tint = tint)
}
