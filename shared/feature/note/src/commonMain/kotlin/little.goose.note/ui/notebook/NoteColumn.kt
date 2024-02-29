package little.goose.note.ui.notebook

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun NoteColumn(
    modifier: Modifier = Modifier,
    state: NoteColumnState,
    onNoteClick: (Long) -> Unit,
    onSelectNote: (item: Long, selected: Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = {
            items(
                items = state.noteItemStateList,
                key = { it.id },
                itemContent = { itemState ->
                    NoteItem(
                        modifier = Modifier.fillMaxWidth(),
                        noteId = itemState.id,
                        title = itemState.title,
                        content = itemState.content,
                        isMultiSelecting = state.isMultiSelecting,
                        isSelected = itemState.isSelected,
                        onNoteClick = onNoteClick,
                        onNoteSelect = onSelectNote
                    )
                }
            )
        }
    )
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    noteId: Long,
    title: String,
    content: String,
    isMultiSelecting: Boolean,
    onNoteSelect: (Long, Boolean) -> Unit,
    isSelected: Boolean,
    onNoteClick: (Long) -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = {
                        if (isMultiSelecting) {
                            onNoteSelect(noteId, !isSelected)
                        } else {
                            onNoteClick(noteId)
                        }
                    },
                    onLongClick = {
                        onNoteSelect(noteId, !isSelected)
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(text = title.ifBlank { "Untitled" })
                Text(text = content)
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "selected",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.5F)
                )
            }
        }
    }
}