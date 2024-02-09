package little.goose.note.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun NoteColumn(
    modifier: Modifier = Modifier,
    itemStates: List<NoteItemState>,
    onNoteItemClick: (noteId: Long) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = itemStates,
            key = NoteItemState::id
        ) {
            NoteItem(
                modifier = Modifier.fillMaxWidth(),
                state = it,
                onClick = onNoteItemClick
            )
        }
    }
}