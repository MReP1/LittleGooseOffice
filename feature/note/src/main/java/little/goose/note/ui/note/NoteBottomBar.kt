package little.goose.note.ui.note

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.AccountTheme
import little.goose.note.logic.FormatType
import little.goose.note.ui.component.FormatHeaderIcon

@Stable
data class NoteBottomBarState(
    val isPreview: Boolean = false,
    val onPreviewChange: (Boolean) -> Unit = {},
    val onFormat: (FormatType) -> Unit = {},
    val onBlockAdd: () -> Unit = {},
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun NoteBottomBar(
    modifier: Modifier = Modifier,
    state: NoteBottomBarState,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.windowInsetsPadding(
                if (WindowInsets.isImeVisible) {
                    WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                } else {
                    BottomAppBarDefaults.windowInsets
                }
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "add note"
                )
            }
            NoteBottomBarRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                state = state
            )
        }
    }
}

@Composable
private fun NoteBottomBarRow(
    modifier: Modifier,
    state: NoteBottomBarState
) {
    Row(modifier) {
        Spacer(modifier = Modifier.width(12.dp))
        val horScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .weight(1F)
                .horizontalScroll(horScrollState)
        ) {
            FormatHeaderIcon(
                modifier = Modifier,
                onHeaderClick = state.onFormat
            )
            IconButton(
                onClick = {
                    state.onFormat(FormatType.List.Unordered)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.FormatListBulleted,
                    contentDescription = "UnorderedList"
                )
            }
            IconButton(
                onClick = {
                    state.onFormat(FormatType.List.Ordered(1))
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.FormatListNumbered,
                    contentDescription = "OrderedList"
                )
            }
            IconButton(
                onClick = {
                    state.onFormat(FormatType.Quote)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.FormatQuote,
                    contentDescription = "Quote"
                )
            }
        }

        FloatingActionButton(
            onClick = {
                state.onPreviewChange(!state.isPreview)
            }
        ) {
            AnimatedContent(
                targetState = state.isPreview,
                label = "fab preview"
            ) { isPreview ->
                Icon(
                    imageVector = if (!isPreview) {
                        Icons.Rounded.Preview
                    } else {
                        Icons.Rounded.EditNote
                    },
                    contentDescription = "Preview"
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}

@Preview
@Composable
private fun PreviewNoteBottomBar() = AccountTheme {
    NoteBottomBar(
        state = NoteBottomBarState(),
        onAddClick = {}
    )
}