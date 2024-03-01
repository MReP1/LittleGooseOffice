package little.goose.note.ui.search

import little.goose.note.ui.notebook.NoteColumnState

sealed interface SearchNoteState {
    data object Loading : SearchNoteState

    data class Success(val data: NoteColumnState) : SearchNoteState

    data object Empty : SearchNoteState
}

