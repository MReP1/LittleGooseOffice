package little.goose.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.note.data.entities.Note
import little.goose.note.logic.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val noteRepository: NoteRepository
): ViewModel() {

    val notes = noteRepository.getAllNoteFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun deleteNoteList(notes: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNoteList(notes)
        }
    }

    fun addNoteList(notes: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.addNoteList(notes)
        }
    }

}