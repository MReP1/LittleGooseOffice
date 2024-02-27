package little.goose.search.note

sealed class SearchNoteIntent {

    data class NotebookIntent(val intent: little.goose.note.ui.notebook.NotebookIntent): SearchNoteIntent()

    data class Search(val keyword: String): SearchNoteIntent()
}