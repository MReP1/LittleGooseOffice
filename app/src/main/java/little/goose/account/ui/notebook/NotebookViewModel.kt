package little.goose.account.ui.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import little.goose.account.logic.NoteRepository

class NotebookViewModel : ViewModel() {

    val notes = NoteRepository.getAllNoteFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

}