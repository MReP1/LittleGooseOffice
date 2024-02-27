package little.goose.note.ui.notebook

sealed class NotebookHomeEvent {

    data object DeleteNote: NotebookHomeEvent()

}