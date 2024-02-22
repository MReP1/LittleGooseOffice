package little.goose.note.ui.note

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EditNote
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
import androidx.compose.ui.unit.dp
import little.goose.note.util.FormatType

@Stable
sealed class NoteBottomBarState {

    data object Loading : NoteBottomBarState()

    data object Preview : NoteBottomBarState()

    data object Editing : NoteBottomBarState()

}

@Composable
fun NoteBottomBar(
    modifier: Modifier = Modifier,
    state: NoteBottomBarState,
    action: (NoteScreenIntent) -> Unit
) {
    Surface(
        modifier = modifier,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state is NoteBottomBarState.Editing) {
                IconButton(onClick = {
                    action(NoteScreenIntent.AddBlockToBottom)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "add note"
                    )
                }
            }
            NoteBottomBarRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                state = state,
                action = action
            )
        }
    }
}

@Composable
private fun NoteBottomBarRow(
    modifier: Modifier,
    state: NoteBottomBarState,
    action: (NoteScreenIntent) -> Unit
) {
    Row(modifier) {
        when (state) {
            is NoteBottomBarState.Preview -> {
                Spacer(modifier = Modifier.weight(1F))
            }

            is NoteBottomBarState.Loading -> {
                // FAB min height.
                Spacer(modifier = Modifier.height(56.dp))
            }

            else -> {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        val horScrollState = rememberScrollState()

        if (state is NoteBottomBarState.Editing) {
            Row(
                modifier = Modifier
                    .weight(1F)
                    .horizontalScroll(horScrollState),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FormatHeaderIcon(
                    modifier = Modifier,
                    onHeaderClick = {
                        action(NoteScreenIntent.Format(it))
                    }
                )
                IconButton(
                    onClick = {
                        action(NoteScreenIntent.Format(FormatType.List.Unordered))
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.FormatListBulleted,
                        contentDescription = "UnorderedList"
                    )
                }
                IconButton(
                    onClick = {
                        action(NoteScreenIntent.Format(FormatType.List.Ordered(1)))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FormatListNumbered,
                        contentDescription = "OrderedList"
                    )
                }
                IconButton(
                    onClick = {
                        action(NoteScreenIntent.Format(FormatType.Quote))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FormatQuote,
                        contentDescription = "Quote"
                    )
                }
            }
        }

        if (state !is NoteBottomBarState.Loading) {
            FloatingActionButton(
                onClick = {
                    when (state) {
                        is NoteBottomBarState.Editing -> action(
                            NoteScreenIntent.ChangeNoteScreenMode(NoteScreenMode.Preview)
                        )

                        is NoteBottomBarState.Preview -> action(
                            NoteScreenIntent.ChangeNoteScreenMode(NoteScreenMode.Edit)
                        )

                        NoteBottomBarState.Loading -> Unit
                    }
                }
            ) {
                AnimatedContent(
                    targetState = state is NoteBottomBarState.Preview,
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
        }

        Spacer(modifier = Modifier.width(12.dp))
    }
}
