package little.goose.data.note.bean

data class NoteWithContent(
    val note: Note,
    val content: List<NoteContentBlock>
)