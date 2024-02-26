package little.goose.data.note.bean

data class NoteContentBlock(
    val id: Long? = null,
    val noteId: Long? = null,
    val content: String = "",
    val sectionIndex: Long = 0,
)