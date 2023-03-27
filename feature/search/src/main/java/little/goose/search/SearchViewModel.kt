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
import little.goose.memorial.ui.component.MemorialColumnState
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

    private val multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())

    private val isTransactionsMultiSelecting = multiSelectedTransactions.map { it.isNotEmpty() }
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
        isTransactionsMultiSelecting
    ) { transactions, multiSelectedTransactions, isMultiSelecting ->
        TransactionColumnState(
            transactions,
            isMultiSelecting,
            multiSelectedTransactions,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelTransactionsMultiSelecting,
            ::deleteTransactions
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TransactionColumnState(
            transactions = (transactionState as? State.Data)?.items?.value ?: emptyList(),
            isTransactionsMultiSelecting.value,
            multiSelectedTransactions.value,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelTransactionsMultiSelecting,
            ::deleteTransactions
        )
    )


    var noteState: State<StateFlow<List<Note>>> by mutableStateOf(State.Empty)
        private set
    var scheduleState: State<StateFlow<List<Schedule>>> by mutableStateOf(State.Empty)
        private set

    private val multiSelectedMemorials = MutableStateFlow<Set<Memorial>>(emptySet())

    private val isMemorialsMultiSelecting = multiSelectedMemorials.map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = false
        )

    var memorialState: State<StateFlow<List<Memorial>>> by mutableStateOf(State.Empty)
        private set

    val memorialColumnState = combine(
        snapshotFlow { memorialState }.flatMapLatest { (it as? State.Data)?.items ?: emptyFlow() },
        multiSelectedMemorials,
        isMemorialsMultiSelecting
    ) { memorials, multiSelectedMemorials, isMemorialsMultiSelecting ->
        MemorialColumnState(
            memorials = memorials,
            isMultiSelecting = isMemorialsMultiSelecting,
            multiSelectedMemorials = multiSelectedMemorials,
            onMemorialSelected = ::selectMemorial,
            selectAllMemorial = ::selectAllMemorial,
            cancelMultiSelecting = ::cancelMemorialsMultiSelecting,
            deleteMemorials = ::deleteMemorials
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        MemorialColumnState(
            memorials = (memorialState as? State.Data)?.items?.value ?: emptyList(),
            isMultiSelecting = isMemorialsMultiSelecting.value,
            multiSelectedMemorials = multiSelectedMemorials.value,
            onMemorialSelected = ::selectMemorial,
            selectAllMemorial = ::selectAllMemorial,
            cancelMultiSelecting = ::cancelMemorialsMultiSelecting,
            deleteMemorials = ::deleteMemorials
        )
    )

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
                            multiSelectedTransactions.value = emptySet()
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

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transaction)
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.deleteTransactions(transactions)
        }
    }

    private fun selectTransaction(transaction: Transaction, selected: Boolean) {
        multiSelectedTransactions.value = multiSelectedTransactions.value.toMutableSet().apply {
            if (selected) add(transaction) else remove(transaction)
        }
    }

    private fun selectAllTransaction() {
        multiSelectedTransactions.value =
            (transactionState as? State.Data)?.items?.value?.toSet() ?: emptySet()
    }

    private fun cancelTransactionsMultiSelecting() {
        multiSelectedTransactions.value = emptySet()
    }

    private fun selectAllMemorial() {
        multiSelectedMemorials.value =
            (memorialState as? State.Data)?.items?.value?.toSet() ?: emptySet()
    }

    private fun cancelMemorialsMultiSelecting() {
        multiSelectedMemorials.value = emptySet()
    }

    private fun selectMemorial(memorial: Memorial, selected: Boolean) {
        multiSelectedMemorials.value = multiSelectedMemorials.value.toMutableSet().apply {
            if (selected) add(memorial) else remove(memorial)
        }
    }

    fun deleteMemorial(memorial: Memorial) {
        viewModelScope.launch {
            memorialRepository.deleteMemorial(memorial)
        }
    }

    private fun deleteMemorials(memorials: List<Memorial>) {
        viewModelScope.launch {
            memorialRepository.deleteMemorials(memorials)
        }
    }

}