package little.goose.note.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import little.goose.note.data.constants.KEY_NOTE
import little.goose.note.data.entities.Note
import little.goose.note.logic.NoteRepository
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private enum class Type {
        ADD, MODIFY
    }

    val note: StateFlow<Note> = savedStateHandle.getStateFlow(KEY_NOTE, Note())

    private val type get() = if (note.value.id == null) Type.ADD else Type.MODIFY

    private val isNoteBlank get() = note.value.title.isBlank() && note.value.content.isBlank()

    val noteScreenState = note.map {
        NoteScreenState(note = it, onTitleChange = ::changeTitle, onContentChange = ::changeContent)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        initialValue = NoteScreenState(note.value, ::changeTitle, ::changeContent)
    )

    private fun changeTitle(title: String) {
        savedStateHandle[KEY_NOTE] = note.value.copy(title = title)
    }

    private fun changeContent(content: String) {
        savedStateHandle[KEY_NOTE] = note.value.copy(content = content)
    }

    init {
        note.drop(1).onEach {
            when (type) {
                Type.ADD -> insertDatabase()
                Type.MODIFY -> updateDatabase()
            }
        }.launchIn(viewModelScope)
    }

    private suspend fun updateDatabase() {
        if (!isNoteBlank) {
            noteRepository.updateNote(note.value)
        } else {
            noteRepository.deleteNote(note.value)
        }
    }

    private suspend fun insertDatabase() {
        if (!isNoteBlank) {
            val id = noteRepository.addNote(note.value)
            savedStateHandle[KEY_NOTE] = note.value.copy(id = id)
        }
    }

}