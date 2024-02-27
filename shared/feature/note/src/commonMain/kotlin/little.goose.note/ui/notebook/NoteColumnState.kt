package little.goose.note.ui.notebook

import androidx.compose.runtime.Stable

@Stable
data class NoteColumnState(
    val noteItemStateList: List<NoteItemState> = emptyList(),
    val isMultiSelecting: Boolean = false,
    val multiSelectedNotes: Set<Long> = emptySet()
)

data class NoteItemState(
    val id: Long,
    val title: String,
    val content: String
)