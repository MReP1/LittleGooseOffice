package little.goose.account.ui.notebook.note

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import little.goose.account.AccountApplication
import little.goose.account.appScope
import little.goose.account.logic.NoteRepository
import little.goose.account.logic.data.entities.Note

class NoteViewModel : ViewModel() {

    suspend fun insertNote(note: Note) = withContext(Dispatchers.IO) {
        NoteRepository.addNote(note)
    }

    fun updateNote(note: Note) {
        appScope.launch {
            NoteRepository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        appScope.launch {
            NoteRepository.deleteNote(note)
        }
    }

}