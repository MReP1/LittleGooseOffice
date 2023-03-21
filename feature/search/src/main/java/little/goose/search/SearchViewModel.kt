package little.goose.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import little.goose.account.logic.AccountRepository
import little.goose.memorial.logic.MemorialRepository
import little.goose.note.logic.NoteRepository
import little.goose.schedule.logic.ScheduleRepository
import javax.inject.Inject
import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.memorial.data.entities.Memorial
import little.goose.note.data.entities.Note
import little.goose.schedule.data.entities.Schedule

@Parcelize
enum class SearchType : Parcelable {
    Transaction, Note, Memorial, Schedule;

    companion object {
        const val KEY_SEARCH_TYPE = "search_type"
    }
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
    private val scheduleRepository: ScheduleRepository,
    private val noteRepository: NoteRepository,
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    val type: SearchType = savedStateHandle[SearchType.KEY_SEARCH_TYPE] ?: SearchType.Transaction

    sealed class State<out T> {
        data class Data<out T>(val items: T) : State<T>()
        object Empty : State<Nothing>()
    }

    var transactionState: State<StateFlow<List<Transaction>>> by mutableStateOf(State.Empty)
        private set
    var noteState: State<StateFlow<List<Note>>> by mutableStateOf(State.Empty)
        private set
    var scheduleState: State<StateFlow<List<Schedule>>> by mutableStateOf(State.Empty)
        private set
    var memorialState: State<StateFlow<List<Memorial>>> by mutableStateOf(State.Empty)
        private set

    fun search(keyword: String) {
        viewModelScope.launch {
            when (type) {
                SearchType.Transaction -> {
                    val transactionStateFlow = if (keyword.toLongOrNull() != null) {
                        accountRepository.searchTransactionByMoneyFlow(money = keyword)
                    } else {
                        accountRepository.searchTransactionByTextFlow(text = keyword)
                    }.stateIn(
                        viewModelScope,
                        SharingStarted.WhileSubscribed(5000),
                        emptyList()
                    )
                    transactionState = State.Data(transactionStateFlow)
                }
                SearchType.Note -> {
                    val noteStateFlow = noteRepository.searchNoteByTextFlow(keyword).stateIn(
                        viewModelScope,
                        SharingStarted.WhileSubscribed(5000),
                        emptyList()
                    )
                    noteState = State.Data(noteStateFlow)
                }
                SearchType.Memorial -> {
                    val memorialStateFlow = memorialRepository.searchMemorialByTextFlow(keyword)
                        .stateIn(
                            viewModelScope,
                            SharingStarted.WhileSubscribed(5000),
                            emptyList()
                        )
                    memorialState = State.Data(memorialStateFlow)
                }
                SearchType.Schedule -> {
                    val scheduleFlow = scheduleRepository.searchScheduleByTextFlow(keyword).stateIn(
                        viewModelScope,
                        SharingStarted.WhileSubscribed(5000),
                        emptyList()
                    )
                    scheduleState = State.Data(scheduleFlow)
                }
            }
        }
    }

}