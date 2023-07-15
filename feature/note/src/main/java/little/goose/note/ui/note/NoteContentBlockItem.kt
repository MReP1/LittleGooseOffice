package little.goose.note.ui.note

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.AccountTheme

@Composable
fun NoteContentBlockItem(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onBlockDelete: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.background
        },
        label = "item background color"
    )
    val dismissState = rememberDismissState()

    LaunchedEffect(dismissState) {
        snapshotFlow {
            dismissState.currentValue
        }.collect { dismissValue ->
            if (dismissValue == DismissValue.DismissedToEnd) {
                onBlockDelete()
            }
        }
    }

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.CenterStart
            ) {
                val targetValue = dismissState.targetValue
                val currentValue = dismissState.currentValue
                val alpha by animateFloatAsState(
                    targetValue = if (
                        targetValue == DismissValue.DismissedToEnd
                        || currentValue == DismissValue.DismissedToEnd
                    ) 1F else 0.5F,
                    label = "delete icon alpha"
                )
                val scale by animateFloatAsState(
                    targetValue = if (
                        targetValue == DismissValue.DismissedToEnd
                        || currentValue == DismissValue.DismissedToEnd
                    ) 1F else 0.72F,
                    label = "delete icon scale"
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .alpha(alpha)
                        .scale(scale),
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete"
                )
            }
        },
        dismissContent = {
            Surface(
                modifier = Modifier,
                color = backgroundColor
            ) {
                NoteContentBlockTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = value,
                    onValueChange = onValueChange,
                    interactionSource = interactionSource,
                    focusRequester = focusRequester
                )
            }
        },
        directions = remember {
            setOf(DismissDirection.StartToEnd)
        }
    )
}

@Composable
fun NoteContentBlockTextField(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = LocalContentColor.current
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 16.dp),
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
        onBlockDelete = {}
    )
}

@Preview
@Composable
private fun PreviewNoteContentBlockTextField() = AccountTheme {
    Surface {
        NoteContentBlockTextField(
            value = TextFieldValue("Content"),
            onValueChange = {}
        )
    }
}