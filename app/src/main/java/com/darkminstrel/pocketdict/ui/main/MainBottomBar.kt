@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package com.darkminstrel.pocketdict.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.darkminstrel.pocketdict.ui.views.IconButton
import kotlinx.coroutines.delay
import com.darkminstrel.pocketdict.R

@Composable
internal fun MainBottomBar(
    modifier: Modifier = Modifier,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
) = Surface(
    modifier = modifier
        .fillMaxWidth()
        .height(56.dp),
    elevation = 4.dp,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(Unit) { delay(100L); focusRequester.requestFocus() }

        BasicTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester)
                .weight(1f),
            value = query,
            onValueChange = onQueryChange,
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            textStyle = MaterialTheme.typography.body1,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            decorationBox = { innerTextField ->
                if (query.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.enterAWordHere),
                        color = MaterialTheme.colors.onSurface.copy(0.5f)
                    )
                }
                innerTextField()
            }
        )
        if (query.text.isNotEmpty()) {
            IconButton(
                imageVector = Icons.Default.Clear,
                onClick = {
                    onQueryChange(TextFieldValue())
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            )
        }
    }
}