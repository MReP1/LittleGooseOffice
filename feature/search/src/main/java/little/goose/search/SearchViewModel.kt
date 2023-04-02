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
import little.goose.note.ui.NoteGridState
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.ui.ScheduleColumnState

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

    sealed class Event {
        data class DeleteTransactions(val transactions: List<Transaction>) : Event()
        data class DeleteNotes(val notes: List<Note>) : Event()
        data class DeleteMemorials(val memorials: List<Memorial>) : Event()
        data class DeleteSchedules(val schedules: List<Schedule>) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val event = _event.asSharedFlow()

    private val multiSelectedTransactions = MutableStateFlow<Set<Transaction>>(emptySet())

    var transactionState: State<StateFlow<List<Transaction>>> by mutableStateOf(State.Empty)
        private set

    val transactionColumnState = combine(
        snapshotFlow { transactionState }.flatMapLatest {
            (it as? State.Data)?.items ?: emptyFlow()
        },
        multiSelectedTransactions
    ) { transactions, multiSelectedTransactions ->
        TransactionColumnState(
            transactions = transactions,
            isMultiSelecting = multiSelectedTransactions.isNotEmpty(),
            multiSelectedTransactions = multiSelectedTransactions,
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
            multiSelectedTransactions.value.isNotEmpty(),
            multiSelectedTransactions.value,
            ::selectTransaction,
            ::selectAllTransaction,
            ::cancelTransactionsMultiSelecting,
            ::deleteTransactions
        )
    )

    private val multiSelectedNotes = MutableStateFlow<Set<Note>>(emptySet())

    var noteState: State<StateFlow<List<Note>>> by mutableStateOf(State.Empty)
        private set

    val noteGridState = combine(
        snapshotFlow { noteState }.flatMapLatest { (it as? State.Data)?.items ?: emptyFlow() },
        multiSelectedNotes
    ) { notes, multiSelectedNotes ->
        NoteGridState(
            notes = notes,
            isMultiSelecting = multiSelectedNotes.isNotEmpty(),
            multiSelectedNotes = multiSelectedNotes,
            ::selectNote,
            ::selectAllNote,
            ::cancelNotesMultiSelecting,
            ::deleteNotes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = NoteGridState(
            notes = (noteState as? State.Data)?.items?.value ?: emptyList(),
            isMultiSelecting = multiSelectedNotes.value.isNotEmpty(),
            multiSelectedNotes = multiSelectedNotes.value,
            ::selectNote,
            ::selectAllNote,
            ::cancelNotesMultiSelecting,
            ::deleteNotes
        )
    )

    private val multiSelectedSchedules = MutableStateFlow<Set<Schedule>>(emptySet())

    var scheduleState: State<StateFlow<List<Schedule>>> by mutableStateOf(State.Empty)
        private set

    val scheduleColumnState = combine(
        snapshotFlow { scheduleState }.flatMapLatest { (it as? State.Data)?.items ?: emptyFlow() },
        multiSelectedSchedules
    ) { schedules, multiSelectedSchedules ->
        ScheduleColumnState(
            schedules = schedules,
            isMultiSelecting = multiSelectedSchedules.isNotEmpty(),
            multiSelectedSchedules = multiSelectedSchedules,
            ::selectSchedule,
            ::checkSchedule,
            ::selectAllSchedule,
            ::cancelSchedulesMultiSelecting,
            ::deleteSchedules
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ScheduleColumnState(
            schedules = (scheduleState as? State.Data)?.items?.value ?: emptyList(),
            isMultiSelecting = multiSelectedSchedules.value.isNotEmpty(),
            multiSelectedSchedules = multiSelectedSchedules.value,
            ::selectSchedule,
            ::checkSchedule,
            ::selectAllSchedule,
            ::cancelSchedulesMultiSelecting,
            ::deleteSchedules
        )
    )

    private val multiSelectedMemorials = MutableStateFlow<Set<Memorial>>(emptySet())

    var memorialState: State<StateFlow<List<Memorial>>> by mutableStateOf(State.Empty)
        private set

    val memorialColumnState = combine(
        snapshotFlow { memorialState }.flatMapLatest { (it as? State.Data)?.items ?: emptyFlow() },
        multiSelectedMemorials
    ) { memorials, multiSelectedMemorials ->
        MemorialColumnState(
            memorials = memorials,
            isMultiSelecting = multiSelectedMemorials.isNotEmpty(),
            multiSelectedMemorials = multiSelectedMemorials,
            onSelectMemorial = ::selectMemorial,
            selectAllMemorial = ::selectAllMemorial,
            cancelMultiSelecting = ::cancelMemorialsMultiSelecting,
            deleteMemorials = ::deleteMemorials
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        MemorialColumnState(
            memorials = (memorialState as? State.Data)?.items?.value ?: emptyList(),
            isMultiSelecting = multiSelectedMemorials.value.isNotEmpty(),
            multiSelectedMemorials = multiSelectedMemorials.value,
            onSelectMemorial = ::selectMemorial,
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

    /**
     * Transaction start
     */

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.deleteTransaction(transaction)
            _event.emit(Event.DeleteTransactions(listOf(transaction)))
        }
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            accountRepository.deleteTransactions(transactions)
            _event.emit(Event.DeleteTransactions(transactions))
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

    /**
     * Memorial start
     */

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
            _event.emit(Event.DeleteMemorials(listOf(memorial)))
        }
    }

    private fun deleteMemorials(memorials: List<Memorial>) {
        viewModelScope.launch {
            memorialRepository.deleteMemorials(memorials)
            _event.emit(Event.DeleteMemorials(memorials))
        }
    }

    /**
     * Schedule start
     */

    private fun selectSchedule(schedule: Schedule, selected: Boolean) {
        multiSelectedSchedules.value = multiSelectedSchedules.value.toMutableSet().apply {
            if (selected) add(schedule) else remove(schedule)
        }
    }

    private fun checkSchedule(schedule: Schedule, checked: Boolean) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule.copy(isfinish = checked))
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
            _event.emit(Event.DeleteSchedules(listOf(schedule)))
        }
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.addSchedule(schedule)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    private fun deleteSchedules(schedules: List<Schedule>) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedules(schedules)
            _event.emit(Event.DeleteSchedules(schedules))
        }
    }

    private fun selectAllSchedule() {
        multiSelectedSchedules.value =
            (scheduleState as? State.Data)?.items?.value?.toSet() ?: emptySet()
    }

    private fun cancelSchedulesMultiSelecting() {
        multiSelectedSchedules.value = emptySet()
    }

    /**
     * Note start
     */
    private fun selectNote(
        note: Note,
        selected: Boolean
    ) {
        multiSelectedNotes.value = multiSelectedNotes.value.toMutableSet().apply {
            if (selected) add(note) else remove(note)
        }
    }

    private fun selectAllNote() {
        multiSelectedNotes.value = (noteState as? State.Data)?.items?.value?.toSet() ?: emptySet()
    }

    private fun cancelNotesMultiSelecting() {
        multiSelectedNotes.value = emptySet()
    }

    private fun deleteNotes(notes: List<Note>) {
        viewModelScope.launch {
            noteRepository.deleteNotes(notes)
            _event.emit(Event.DeleteNotes(notes))
        }
    }

}