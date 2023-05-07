package little.goose.note.ui.note

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.AccountTheme

@Composable
fun NoteContentBlockItem(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onBlockAdd: () -> Unit,
    onBlockDelete: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        }
    )
    Row(
        modifier = modifier.background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NoteContentBlockTextField(
            modifier = Modifier.weight(1F),
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            focusRequester = focusRequester,
            onBlockAdd = onBlockAdd
        )
        if (isFocused) {
            IconButton(
                onClick = onBlockDelete,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
fun NoteContentBlockTextField(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onBlockAdd: () -> Unit
) {
    val currentOnBlockAdd by rememberUpdatedState(newValue = onBlockAdd)

    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource,
        keyboardOptions = remember { KeyboardOptions(imeAction = ImeAction.Next) },
        keyboardActions = remember { KeyboardActions(onNext = { currentOnBlockAdd() }) },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                innerTextField()
            }
        }
    )
}

@Preview
@Composable
private fun PreviewNoteBlockBlockItem() = AccountTheme {
    NoteContentBlockItem(
        value = TextFieldValue("Content"),
        onValueChange = {},
        onBlockAdd = {},
        onBlockDelete = {}
    )
}

@Preview
@Composable
private fun PreviewNoteContentBlockTextField() = AccountTheme {
    Surface {
        NoteContentBlockTextField(
            value = TextFieldValue("Content"),
            onValueChange = {},
            onBlockAdd = {}
        )
    }
}