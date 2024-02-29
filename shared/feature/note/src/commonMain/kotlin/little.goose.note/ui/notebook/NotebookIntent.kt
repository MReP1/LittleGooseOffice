package little.goose.note.ui.notebook

sealed class NotebookIntent {

    data object SelectAllNotes : NotebookIntent()

    data object CancelMultiSelecting : NotebookIntent()

    data object DeleteMultiSelectingNotes : NotebookIntent()

    data class SelectNote(
        val noteId: Long,
        val selected: Boolean
    ) : NotebookIntent()

}