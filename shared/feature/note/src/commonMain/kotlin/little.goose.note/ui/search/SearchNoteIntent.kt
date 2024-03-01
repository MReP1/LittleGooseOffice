package little.goose.note.ui.search

sealed class SearchNoteIntent {

    data class NotebookIntent(
        val intent: little.goose.note.ui.notebook.NotebookIntent
    ) : SearchNoteIntent()

    data class Search(val keyword: String) : SearchNoteIntent()
}

