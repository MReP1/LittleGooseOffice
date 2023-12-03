package little.goose.note.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import little.goose.note.data.constants.TABLE_NOTE_CONTENT_BLOCK

@Entity(tableName = TABLE_NOTE_CONTENT_BLOCK)
data class NoteContentBlock(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo("note_id")
    val noteId: Long? = null,
    @ColumnInfo("index")
    val index: Int = 0,
    @ColumnInfo("content")
    val content: String = ""
)
