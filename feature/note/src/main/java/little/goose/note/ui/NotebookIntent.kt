package little.goose.note.ui

sealed class NotebookIntent {

    data object SelectAllNotes : NotebookIntent()

    data object CancelMultiSelecting : NotebookIntent()

    data class DeleteNotes(
        val noteIds: List<Long>
    ) : NotebookIntent()

    data class SelectNote(
        val noteId: Long,
        val selectNote: Boolean
    ) : NotebookIntent()

}