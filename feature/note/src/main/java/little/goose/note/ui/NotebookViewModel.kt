package little.goose.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.data.note.bean.Note
import little.goose.data.note.domain.DeleteNoteAndItsBlocksListUseCase
import little.goose.data.note.domain.GetNoteWithContentFlowUseCase

class NotebookViewModel(
    getNoteWithContentFlowUseCase: GetNoteWithContentFlowUseCase,
    private val deleteNoteAndItsBlocksListUseCase: DeleteNoteAndItsBlocksListUseCase
) : ViewModel() {

    sealed class Event {
        data class DeleteNotes(val notes: List<Note>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedNotes = MutableStateFlow<Set<Long>>(emptySet())

    private val noteWithContents = getNoteWithContentFlowUseCase().map {
        buildMap {
            for (nwc in it) {
                put(nwc.note, nwc.content)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap()
    )

    val noteColumnState = combine(
        multiSelectedNotes, noteWithContents
    ) { multiSelectedNotes, noteWithContents ->
        NoteColumnState(
            noteItemStateList = noteWithContents.map { (note, noteContentBlocks) ->
                NoteItemState(note.id!!, note.title, noteContentBlocks.firstOrNull()?.content ?: "")
            },
            multiSelectedNotes = multiSelectedNotes,
            isMultiSelecting = multiSelectedNotes.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NoteColumnState(
            noteItemStateList = noteWithContents.value.map { (note, noteContentBlocks) ->
                NoteItemState(note.id!!, note.title, noteContentBlocks.firstOrNull()?.content ?: "")
            },
            multiSelectedNotes = multiSelectedNotes.value,
            isMultiSelecting = multiSelectedNotes.value.isNotEmpty()
        )
    )

    fun action(intent: NotebookIntent) {
        when (intent) {
            NotebookIntent.CancelMultiSelecting -> cancelMultiSelecting()
            NotebookIntent.SelectAllNotes -> selectAllNotes()
            is NotebookIntent.DeleteNotes -> deleteNotes(intent.noteIds)
            is NotebookIntent.SelectNote -> selectNote(intent.noteId, intent.selectNote)
        }
    }

    private fun selectNote(noteId: Long, selected: Boolean) {
        multiSelectedNotes.value = multiSelectedNotes.value.toMutableSet().apply {
            if (selected) add(noteId) else remove(noteId)
        }
    }

    private fun selectAllNotes() {
        multiSelectedNotes.value = noteWithContents.value.keys.mapNotNull { it.id }.toSet()
    }

    private fun cancelMultiSelecting() {
        multiSelectedNotes.value = emptySet()
    }

    private fun deleteNotes(notes: List<Long>) {
        viewModelScope.launch {
            deleteNoteAndItsBlocksListUseCase(notes)
            cancelMultiSelecting()
        }
    }

}