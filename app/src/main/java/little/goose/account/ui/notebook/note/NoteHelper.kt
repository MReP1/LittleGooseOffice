package little.goose.account.ui.notebook.note

import little.goose.account.logic.NoteRepository
import little.goose.account.logic.data.entities.Note

object NoteHelper {

    private var noteList: List<Note>? = null

    suspend fun initNote() {
        noteList = NoteRepository.getAllNote()
    }

    fun getNoteList(): List<Note> {
        return noteList ?: emptyList()
    }

    fun setNoteList(list: List<Note>) {
        noteList = list
    }

}