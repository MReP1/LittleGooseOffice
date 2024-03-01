package little.goose.note.ui.search

import androidx.compose.runtime.saveable.Saver
import little.goose.note.ui.notebook.NoteColumnState

sealed interface SearchNoteState {
    data object Loading : SearchNoteState

    data class Success(val keyword: String, val data: NoteColumnState) : SearchNoteState

    data object Empty : SearchNoteState

    companion object {
        val saver = Saver<SearchNoteState, Any>(
            save = { state ->
                when (state) {
                    Empty -> listOf(0)
                    Loading -> listOf(1)
                    is Success -> listOf(2, state.keyword, state.data.toSavable())
                }
            },
            restore = {
                val savable = it as List<*>
                val type = savable[0] as Int
                when (type) {
                    1 -> Loading
                    2 -> Success(
                        savable[1] as String,
                        NoteColumnState.fromSavable(savable[2] as Any)
                    )

                    else -> Empty
                }
            }
        )

    }
}

