package little.goose.note.ui.notebook

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import little.goose.shared.common.CommonParcelable
import little.goose.shared.common.CommonParcelize

@Stable
data class NoteColumnState(
    val noteItemStateList: List<NoteItemState> = emptyList(),
    val isMultiSelecting: Boolean = false
) {
    companion object {

        val saver = Saver<NoteColumnState, Any>(
            save = { it.toSavable() },
            restore = { fromSavable(it) }
        )

        @Suppress("UNCHECKED_CAST")
        fun fromSavable(savable: Any): NoteColumnState {
            val (isMultiSelecting, saveItems) = savable as List<*>
            val items = saveItems as List<NoteItemState>
            return NoteColumnState(
                isMultiSelecting = isMultiSelecting as Boolean,
                noteItemStateList = items
            )
        }
    }

    fun toSavable(): Any {
        return listOf(isMultiSelecting, noteItemStateList)
    }
}

@CommonParcelize
@Stable
data class NoteItem(
    val id: Long,
    val title: String,
    val content: String
): CommonParcelable

@CommonParcelize
@Stable
data class NoteItemState(
    val id: Long,
    val title: String,
    val content: String,
    val isSelected: Boolean
): CommonParcelable