package little.goose.data.note.bean

import littlegoosenote.GooseNoteContentBlock

data class NoteWithContent(
    val note: Note,
    val content: List<GooseNoteContentBlock>
)