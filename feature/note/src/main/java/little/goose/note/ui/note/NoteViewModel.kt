package little.goose.note.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import little.goose.note.data.entities.Note
import little.goose.note.logic.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    fun insertNote(note: Note, onInsert: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            val id = noteRepository.addNote(note)
            onInsert(id)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            noteRepository.deleteNote(note)
        }
    }

}