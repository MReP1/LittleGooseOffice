package little.goose.data.note.bean

import littlegoosenote.GooseNote
import littlegoosenote.GooseNoteContentBlock

data class NoteWithContent(
    val note: GooseNote,
    val content: List<GooseNoteContentBlock>
)