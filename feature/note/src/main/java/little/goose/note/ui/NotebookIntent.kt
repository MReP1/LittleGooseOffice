package little.goose.note.ui

import little.goose.note.data.entities.Note

sealed class NotebookIntent {

    data object SelectAllNotes : NotebookIntent()

    data object CancelMultiSelecting : NotebookIntent()

    data class DeleteNotes(
        val notes: List<Note>
    ) : NotebookIntent()

    data class SelectNote(
        val note: Note,
        val selectNote: Boolean
    ) : NotebookIntent()

}