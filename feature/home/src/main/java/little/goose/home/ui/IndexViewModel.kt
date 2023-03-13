package little.goose.home.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    private val firstVisibleMonth: MutableStateFlow<YearMonth> = MutableStateFlow(YearMonth.now())
    private val lastVisibleMonth: MutableStateFlow<YearMonth> =
        MutableStateFlow(firstVisibleMonth.value.plusMonths(1))

    private val zoneId by lazy { ZoneId.systemDefault() }

    private val calendarModelMap = mutableMapOf<LocalDate, MutableState<CalendarModel>>()

    fun getCalendarModelState(time: LocalDate): MutableState<CalendarModel> {
        return calendarModelMap[time] ?: mutableStateOf(CalendarModel()).also {
            calendarModelMap[time] = it
        }
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
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
        var expense = BigDecimal(0)
        var income = BigDecimal(0)
        transactions.forEach { transaction ->
            val time = transaction.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(transaction)
            if (transaction.type == EXPENSE) {
                expense -= transaction.money
            } else if (transaction.type == INCOME) {
                income += transaction.money
            }
        }
        map.forEach { (time, transactions) ->
            val calendarModelState = getCalendarModelState(time)
            calendarModelState.value = calendarModelState.value.copy(
                transactions = transactions,
                expense = expense,
                income = income,
                balance = expense + income
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

    fun updateTime(
        firstVisibleMonth: YearMonth,
        lastVisibleMonth: YearMonth
    ) {
        this.firstVisibleMonth.value = firstVisibleMonth
        this.lastVisibleMonth.value = lastVisibleMonth
    }

}