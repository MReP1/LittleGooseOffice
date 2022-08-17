package little.goose.account.ui.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import little.goose.account.logic.NoteRepository

class NotebookViewModel: ViewModel() {

    suspend fun getAllNoteAsFlow() = NoteRepository.getAllNoteFlow().stateIn(viewModelScope)
}