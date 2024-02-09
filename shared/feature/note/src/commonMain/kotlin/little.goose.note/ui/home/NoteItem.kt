package little.goose.note.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Stable
internal data class NoteItemState(
    val id: Long,
    val title: String,
    val content: String
)

@Composable
internal fun NoteItem(
    modifier: Modifier = Modifier,
    state: NoteItemState,
    onClick: (noteId: Long) -> Unit
) {
    Surface(
        onClick = { onClick(state.id) },
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(state.title)
            Text(state.content)
        }
    }
}