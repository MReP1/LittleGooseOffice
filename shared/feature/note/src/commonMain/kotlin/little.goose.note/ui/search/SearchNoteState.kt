package little.goose.note.ui.search

import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import little.goose.note.ui.notebook.NoteColumnState

@Stable
data class SearchNoteScreenState(
    val keyword: String,
    val state: SearchNoteState
)

@Stable
sealed interface SearchNoteState {

    @Stable
    data object Loading : SearchNoteState

    @Stable
    data class Success(val data: NoteColumnState) : SearchNoteState

    @Stable
    data object Empty : SearchNoteState

    companion object {
        val saver = Saver<SearchNoteState, Any>(
            save = { state ->
                when (state) {
                    Empty -> listOf(0)
                    Loading -> listOf(1)
                    is Success -> listOf(2, state.data.toSavable())
                }
            },
            restore = {
                val savable = it as List<*>
                val type = savable[0] as Int
                when (type) {
                    1 -> Loading
                    2 -> Success(NoteColumnState.fromSavable(savable[1] as Any))
                    else -> Empty
                }
            }
        )

    }
}

