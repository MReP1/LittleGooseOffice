package little.goose.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import little.goose.account.logic.AccountRepository
import little.goose.memorial.logic.MemorialRepository
import little.goose.note.logic.NoteRepository
import little.goose.schedule.logic.ScheduleRepository
import javax.inject.Inject
import android.os.Parcelable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumnState
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

    private val _multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())
    val multiSelectedTransactions = _multiSelectedTransactions.asStateFlow()

    private val isMultiSelecting = multiSelectedTransactions.map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    var transactionState: State<StateFlow<List<Transaction>>> by mutableStateOf(State.Empty)
        private set

    val transactionColumnState = combine(
        snapshotFlow { transactionState }.flatMapLatest {
            (it as? State.Data)?.items ?: emptyFlow()
        },
        multiSelectedTransactions,
        isMultiSelecting
    ) { transactions, multiSelectedTransactions, isMultiSelecting ->
        TransactionColumnState(
            transactions,
            isMultiSelecting,
            multiSelectedTransactions,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelMultiSelecting,
            ::deleteTransactions
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TransactionColumnState(
            transactions = (transactionState as? State.Data)?.items?.value ?: emptyList(),
            isMultiSelecting.value,
            multiSelectedTransactions.value,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelMultiSelecting,
            ::deleteTransactions
        )
    )


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
                    transactionState = if (keyword.isBlank()) State.Empty else {
                        val transactionStateFlow = if (keyword.toLongOrNull() != null) {
                            accountRepository.searchTransactionByMoneyFlow(money = keyword)
                        } else {
                            accountRepository.searchTransactionByTextFlow(text = keyword)
                        }.onEach {
                            _multiSelectedTransactions.value = emptySet()
                        }.stateIn(
                            viewModelScope,
                            SharingStarted.WhileSubscribed(5000),
                            emptyList()
                        )
                        State.Data(transactionStateFlow)
                    }
                }
                SearchType.Note -> {
                    noteState = if (keyword.isBlank()) State.Empty else {
                        val noteStateFlow = noteRepository.searchNoteByTextFlow(keyword).stateIn(
                            viewModelScope,
                            SharingStarted.WhileSubscribed(5000),
                            emptyList()
                        )
                        State.Data(noteStateFlow)
                    }
                }
                SearchType.Memorial -> {
                    memorialState = if (keyword.isBlank()) State.Empty else {
                        val memorialStateFlow = memorialRepository.searchMemorialByTextFlow(keyword)
                            .stateIn(
                                viewModelScope,
                                SharingStarted.WhileSubscribed(5000),
                                emptyList()
                            )
                        State.Data(memorialStateFlow)
                    }
                }
                SearchType.Schedule -> {
                    scheduleState = if (keyword.isBlank()) State.Empty else {
                        val scheduleFlow =
                            scheduleRepository.searchScheduleByTextFlow(keyword).stateIn(
                                viewModelScope,
                                SharingStarted.WhileSubscribed(5000),
                                emptyList()
                            )
                        State.Data(scheduleFlow)
                    }
                }
            }
        }
    }

    private fun selectTransaction(transaction: Transaction, selected: Boolean) {
        _multiSelectedTransactions.value = _multiSelectedTransactions.value.toMutableSet().apply {
            if (selected) add(transaction) else remove(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transaction)
        }
    }

    fun deleteMemorial(memorial: Memorial) {
        viewModelScope.launch {
            memorialRepository.deleteMemorial(memorial)
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.deleteTransactions(transactions)
        }
    }

    private fun selectAllTransaction() {
        _multiSelectedTransactions.value =
            (transactionState as? State.Data)?.items?.value?.toSet() ?: emptySet()
    }

    private fun cancelMultiSelecting() {
        _multiSelectedTransactions.value = emptySet()
    }

}