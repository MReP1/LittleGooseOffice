package little.goose.note.ui.search

sealed interface SearchNoteEvent {
    data object DeleteNotes : SearchNoteEvent
}