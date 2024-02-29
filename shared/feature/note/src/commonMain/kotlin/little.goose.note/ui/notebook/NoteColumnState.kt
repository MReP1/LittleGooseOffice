package little.goose.note.ui.notebook

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver

@Stable
data class NoteColumnState(
    val noteItemStateList: List<NoteItemState> = emptyList(),
    val isMultiSelecting: Boolean = false
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        val saver = Saver<NoteColumnState, Any>(
            save = {
                listOf(it.isMultiSelecting, it.noteItemStateList)
            },
            restore = {
                val (isMultiSelecting, saveItems) = it as List<*>
                val items = saveItems as List<NoteItemState>
                NoteColumnState(
                    isMultiSelecting = isMultiSelecting as Boolean,
                    noteItemStateList = items
                )
            }
        )
    }
}

@Stable
data class NoteItemState(
    val id: Long,
    val title: String,
    val content: String,
    val isSelected: Boolean
)