package little.goose.note.ui.note

import little.goose.note.util.FormatType

enum class NoteScreenMode {
    Preview, Edit
}

sealed class NoteScreenIntent {

    data class Format(val formatType: FormatType) : NoteScreenIntent()

    data class ChangeNoteScreenMode(val mode: NoteScreenMode) : NoteScreenIntent()

    data object AddBlockToBottom : NoteScreenIntent()

    data class DeleteBlock(val id: Long) : NoteScreenIntent()

}