package com.darkminstrel.pocketdict.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkminstrel.pocketdict.ui.theme.PocketTheme

@Composable
fun ListItemTranslation(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    maxLines: Int = Int.MAX_VALUE,
) = Column(
    modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
) {
    val titleAnnotated = remember(title) { title.parseBold() }
    val subtitleAnnotated = remember(subtitle) { subtitle.parseBold() }
    Text(
        text = titleAnnotated,
        style = MaterialTheme.typography.subtitle1,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
    Text(
        text = subtitleAnnotated,
        style = MaterialTheme.typography.subtitle1,
        color = MaterialTheme.colors.primary,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}


private fun String.parseBold(): AnnotatedString {
    val parts = this.split("<b>", "</b>")
    return buildAnnotatedString {
        var bold = false
        for (part in parts) {
            if (bold) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
            bold = !bold
        }
    }
}

@Preview
@Composable
private fun PreviewListItemTranslation() = PocketTheme {
    ListItemTranslation(title = "Title <b>bold</b>", subtitle = "Subtitle")
}

