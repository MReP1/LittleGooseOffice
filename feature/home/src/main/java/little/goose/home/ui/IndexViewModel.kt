package little.goose.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.home.data.CalendarModel
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.MemorialRepository
import little.goose.note.data.entities.Note
import little.goose.note.logic.NoteRepository
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.ScheduleRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val scheduleRepository: ScheduleRepository,
    private val noteRepository: NoteRepository,
    private val memorialRepository: MemorialRepository
) : ViewModel() {

    private val zoneId by lazy { ZoneId.systemDefault() }

    private val calendarModelMap = mutableMapOf<LocalDate, MutableState<CalendarModel>>()
    private fun getCalendarModelState(time: LocalDate): MutableState<CalendarModel> {
        return calendarModelMap.getOrPut(time) { mutableStateOf(CalendarModel()) }
    }

    private val firstVisibleMonth: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())
    private val lastVisibleMonth: MutableStateFlow<YearMonth> =
        MutableStateFlow(firstVisibleMonth.value.plusMonths(1))

    private val currentDay: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    private val currentCalendarModel: StateFlow<MutableState<CalendarModel>> = currentDay.map {
        getCalendarModelState(it)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), mutableStateOf(CalendarModel())
    )

    val indexScreenState = combine(
        currentDay, currentCalendarModel
    ) { currentDay, currentCalendarModelState ->
        IndexScreenState(
            today = LocalDate.now(),
            currentDay = currentDay,
            currentCalendarModel = currentCalendarModelState,
            updateMonth = ::updateMonth,
            updateCurrentDay = ::updateCurrentDay,
            checkSchedule = ::checkSchedule,
            getCalendarModelState = ::getCalendarModelState
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        initialValue = IndexScreenState(
            today = LocalDate.now(), currentDay.value, currentCalendarModel.value,
            ::updateMonth, ::updateCurrentDay, ::checkSchedule, ::getCalendarModelState
        )
    )

    init {
        // FIXME 优化性能
        viewModelScope.launch {
            launch {
                firstVisibleMonth.flatMapLatest {
                    accountRepository.getTransactionByYearMonthFlow(it.year, it.month.value)
                }.collect { transactions: List<Transaction> ->
                    updateTransactions(transactions)
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    accountRepository.getTransactionByYearMonthFlow(it.year, it.month.value)
                }.collect { transactions: List<Transaction> ->
                    updateTransactions(transactions)
                }
            }
            launch {
                firstVisibleMonth.flatMapLatest {
                    scheduleRepository.getScheduleByYearMonthFlow(it.year, it.month.value)
                }.collect { schedules ->
                    updateSchedules(schedules)
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    scheduleRepository.getScheduleByYearMonthFlow(it.year, it.month.value)
                }.collect { schedules ->
                    updateSchedules(schedules)
                }
            }
            launch {
                firstVisibleMonth.flatMapLatest {
                    memorialRepository.getMemorialsByYearMonthFlow(it.year, it.month.value)
                }.collect { memorials ->
                    updateMemorials(memorials)
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    memorialRepository.getMemorialsByYearMonthFlow(it.year, it.month.value)
                }.collect { memorials ->
                    updateMemorials(memorials)
                }
            }
            launch {
                firstVisibleMonth.flatMapLatest {
                    noteRepository.getNoteByYearMonthFlow(it.year, it.month.value)
                }.collect { notes ->
                    updateNotes(notes)
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    noteRepository.getNoteByYearMonthFlow(it.year, it.month.value)
                }.collect { notes ->
                    updateNotes(notes)
                }
            }
        }
    }

    private fun updateTransactions(transactions: List<Transaction>) {
        val map = mutableMapOf<LocalDate, MutableList<Transaction>>()
        val expenseMap = mutableMapOf<LocalDate, BigDecimal>()
        val incomeMap = mutableMapOf<LocalDate, BigDecimal>()
        transactions.forEach { transaction ->
            val time = transaction.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(transaction)
            if (transaction.type == EXPENSE) {
                var expense = expenseMap.getOrDefault(time, BigDecimal(0))
                expense -= transaction.money
                expenseMap[time] = expense
            } else if (transaction.type == INCOME) {
                var income = incomeMap.getOrDefault(time, BigDecimal(0))
                income += transaction.money
                incomeMap[time] = income
            }
        }
        map.forEach { (time, transactions) ->
            val calendarModelState = getCalendarModelState(time)
            val expense = expenseMap.getOrDefault(time, BigDecimal(0))
            val income = incomeMap.getOrDefault(time, BigDecimal(0))
            calendarModelState.value = calendarModelState.value.copy(
                transactions = transactions,
                expense = expense,
                income = income,
                balance = income - expense
            )
        }
    }

    private fun updateSchedules(schedules: List<Schedule>) {
        val map = mutableMapOf<LocalDate, MutableList<Schedule>>()
        schedules.forEach { schedule ->
            val time = schedule.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(schedule)
        }
        map.forEach { (time, schedules) ->
            val calendarModelState = getCalendarModelState(time)
            calendarModelState.value = calendarModelState.value.copy(schedules = schedules)
        }
    }

    private fun updateMemorials(memorials: List<Memorial>) {
        val map = mutableMapOf<LocalDate, MutableList<Memorial>>()
        memorials.forEach { memorial ->
            val time = memorial.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(memorial)
        }
        map.forEach { (time, memorials) ->
            val calendarModelState = getCalendarModelState(time)
            calendarModelState.value = calendarModelState.value.copy(memorials = memorials)
        }
    }

    private fun updateNotes(notes: List<Note>) {
        val map = mutableMapOf<LocalDate, MutableList<Note>>()
        notes.forEach { note ->
            val time = note.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(note)
        }
        map.forEach { (time, notes) ->
            val calendarModelState = getCalendarModelState(time)
            calendarModelState.value = calendarModelState.value.copy(notes = notes)
        }
    }

    private fun updateMonth(
        firstVisibleMonth: YearMonth,
        lastVisibleMonth: YearMonth
    ) {
        this.firstVisibleMonth.value = firstVisibleMonth
        this.lastVisibleMonth.value = lastVisibleMonth
    }

    private fun updateCurrentDay(day: LocalDate) {
        currentDay.value = day
    }

    private fun checkSchedule(schedule: Schedule, checked: Boolean) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule.copy(isfinish = checked))
        }
    }

    fun deleteMemorial(memorial: Memorial) {
        viewModelScope.launch {
            memorialRepository.deleteMemorial(memorial)
        }
    }

}