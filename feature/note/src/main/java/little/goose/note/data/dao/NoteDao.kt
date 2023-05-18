package little.goose.note.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import little.goose.note.data.constants.TABLE_NOTE
import little.goose.note.data.constants.TABLE_NOTE_CONTENT_BLOCK
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock

@Dao
interface NoteDao {
    @Query("SELECT * FROM $TABLE_NOTE WHERE time > :startTime and time < :endTime ORDER BY time DESC")
    fun getNoteByTimeFlow(startTime: Long, endTime: Long): Flow<List<Note>>

    @Insert
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteNoteList(noteList: List<Note>)

    @Query("SELECT * FROM $TABLE_NOTE WHERE id = :noteId")
    fun getNoteFlow(noteId: Long): Flow<Note>

    @Transaction
    @Query(
        "SELECT * FROM $TABLE_NOTE " +
                "JOIN $TABLE_NOTE_CONTENT_BLOCK " +
                "ON $TABLE_NOTE.id = $TABLE_NOTE_CONTENT_BLOCK.note_id " +
                "WHERE $TABLE_NOTE.id = :noteId " +
                "ORDER BY $TABLE_NOTE_CONTENT_BLOCK.`index` ASC"
    )
    fun getNoteWithContentMapFlow(noteId: Long): Flow<Map<Note, List<NoteContentBlock>>>

    @Transaction
    @Query(
        "SELECT * FROM $TABLE_NOTE " +
                "JOIN $TABLE_NOTE_CONTENT_BLOCK " +
                "ON $TABLE_NOTE.id = $TABLE_NOTE_CONTENT_BLOCK.note_id " +
                "ORDER BY $TABLE_NOTE_CONTENT_BLOCK.`index` ASC"
    )
    fun getNoteWithContentMapFlow(): Flow<Map<Note, List<NoteContentBlock>>>

    @Transaction
    @Query(
        "SELECT * FROM $TABLE_NOTE " +
                "JOIN $TABLE_NOTE_CONTENT_BLOCK " +
                "ON $TABLE_NOTE.id = $TABLE_NOTE_CONTENT_BLOCK.note_id " +
                "WHERE $TABLE_NOTE.title LIKE '%'|| :keyWord ||'%' OR $TABLE_NOTE_CONTENT_BLOCK.content LIKE '%'|| :keyWord ||'%' " +
                "ORDER BY $TABLE_NOTE_CONTENT_BLOCK.`index` ASC"
    )
    fun getNoteWithContentMapFlowByKeyword(keyWord: String): Flow<Map<Note, List<NoteContentBlock>>>

    @Insert
    suspend fun insertNoteContentBlock(noteContentBlock: NoteContentBlock): Long

    @Delete
    suspend fun deleteNoteContentBlock(noteContentBlock: NoteContentBlock)

    @Query("DELETE FROM $TABLE_NOTE_CONTENT_BLOCK WHERE note_id = :noteId")
    suspend fun deleteNoteContentBlocks(noteId: Long)

    @Query("DELETE FROM $TABLE_NOTE_CONTENT_BLOCK WHERE note_id IN (:noteIdList)")
    suspend fun deleteNoteContentBlocks(noteIdList: List<Long>)

    @Update
    suspend fun updateNoteContentBlock(noteContentBlock: NoteContentBlock)

    @Update
    suspend fun updateNoteContentBlocks(noteContentBlocks: List<NoteContentBlock>)
}