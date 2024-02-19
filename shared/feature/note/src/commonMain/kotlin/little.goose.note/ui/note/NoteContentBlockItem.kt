package little.goose.note.ui.note

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteContentBlockItem(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    onBlockDelete: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val tonalElevation by animateDpAsState(
        targetValue = if (isFocused) 3.dp else 0.3.dp,
        label = "block item tonal elevation"
    )
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState) {
        snapshotFlow {
            dismissState.currentValue
        }.collect { dismissValue: SwipeToDismissBoxValue ->
            if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                onBlockDelete()
            }
        }
    }

    SwipeToDismissBox(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large),
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.CenterStart
            ) {
                val targetValue = dismissState.targetValue
                val currentValue = dismissState.currentValue
                val swipingToEnd = targetValue == SwipeToDismissBoxValue.StartToEnd
                        || currentValue == SwipeToDismissBoxValue.StartToEnd
                val alpha by animateFloatAsState(
                    targetValue = if (swipingToEnd) 1F else 0.5F,
                    label = "delete icon alpha"
                )
                val scale by animateFloatAsState(
                    targetValue = if (swipingToEnd) 1F else 0.72F,
                    label = "delete icon scale"
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .alpha(alpha)
                        .scale(scale),
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete"
                )
            }
        },
        content = {
            NoteContentBlockTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = textFieldState,
                tonalElevation = tonalElevation,
                interactionSource = interactionSource,
                focusRequester = focusRequester
            )
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteContentBlockTextField(
    modifier: Modifier,
    textFieldState: TextFieldState,
    tonalElevation: Dp = 0.dp,
    focusRequester: FocusRequester = remember { FocusRequester() },
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        tonalElevation = tonalElevation
    ) {
        BasicTextField2(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester),
            state = textFieldState,
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = LocalContentColor.current
            ),
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}