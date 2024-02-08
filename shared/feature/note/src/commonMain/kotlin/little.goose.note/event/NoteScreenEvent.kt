package little.goose.note.event

sealed class NoteScreenEvent {
    data class AddNoteBlock(val id: Long) : NoteScreenEvent()
}