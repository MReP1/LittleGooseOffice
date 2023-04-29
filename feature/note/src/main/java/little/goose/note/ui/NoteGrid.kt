package little.goose.note.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mikepenz.markdown.Markdown
import little.goose.note.data.entities.Note
import middle.goose.richtext.RichTextView

data class NoteGridState(
    val notes: List<Note>,
    val isMultiSelecting: Boolean,
    val multiSelectedNotes: Set<Note>,
    val onSelectNote: (item: Note, selected: Boolean) -> Unit,
    val selectAllNotes: () -> Unit,
    val cancelMultiSelecting: () -> Unit,
    val deleteNotes: (notes: List<Note>) -> Unit
)

@Composable
fun NoteGrid(
    modifier: Modifier = Modifier,
    state: NoteGridState,
    onNoteClick: (Note) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        content = {
            items(
                items = state.notes,
                key = { it.id ?: -1 }
            ) { note ->
                NoteItem(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(160.dp),
                    note = note,
                    onNoteSelect = state.onSelectNote,
                    isMultiSelecting = state.isMultiSelecting,
                    isSelected = state.multiSelectedNotes.contains(note),
                    onNoteClick = onNoteClick
                )
            }
        }
    )
}

@Composable
fun NoteItem(
    modifier: Modifier = Modifier,
    note: Note,
    isMultiSelecting: Boolean,
    onNoteSelect: (Note, Boolean) -> Unit,
    isSelected: Boolean,
    onNoteClick: (Note) -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(
                    onClick = {
                        if (isMultiSelecting) {
                            onNoteSelect(note, !isSelected)
                        } else {
                            onNoteClick(note)
                        }
                    },
                    onLongClick = {
                        onNoteSelect(note, !isSelected)
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)

            ) {
                Text(text = note.title)
                Markdown(
                    content = note.content,
                    modifier = Modifier.weight(1F)
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.tertiary,
                    contentDescription = "selected",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5F)
                )
            }
        }
    }
}