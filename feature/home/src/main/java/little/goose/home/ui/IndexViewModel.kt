package little.goose.home.ui

import android.os.Trace
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.atStartOfMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.GetTransactionByYearMonthFlowUseCase
import little.goose.home.data.CalendarModel
import little.goose.home.ui.component.IndexTopBarState
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.logic.GetMemorialsByYearMonthFlowUseCase
import little.goose.schedule.data.entities.Schedule
import little.goose.schedule.logic.GetScheduleByYearMonthFlowUseCase
import little.goose.schedule.logic.UpdateScheduleUseCase
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val getTransactionByYearMonthFlowUseCase: GetTransactionByYearMonthFlowUseCase,
    private val getScheduleByYearMonthFlowUseCase: GetScheduleByYearMonthFlowUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase,
    private val getMemorialByYearMonthFlowUseCase: GetMemorialsByYearMonthFlowUseCase
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
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = getCalendarModelState(currentDay.value)
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

    val indexTopBarState = currentDay.map {
        IndexTopBarState(it, LocalDate.now(), ::updateCurrentDay)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        initialValue = IndexTopBarState(currentDay.value, LocalDate.now(), ::updateCurrentDay)
    )

    init {
        // TODO 有优化的空间，但是暂时没想好怎么优化
        viewModelScope.launch {
            launch {
                firstVisibleMonth.flatMapLatest {
                    getTransactionByYearMonthFlowUseCase(it.year, it.month.value)
                }.collect { transactions: List<Transaction> ->
                    Trace.beginSection("updateTransactions")
                    updateTransactions(transactions, firstVisibleMonth.value)
                    Trace.endSection()
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    getTransactionByYearMonthFlowUseCase(it.year, it.month.value)
                }.collect { transactions: List<Transaction> ->
                    Trace.beginSection("updateTransactions")
                    updateTransactions(transactions, lastVisibleMonth.value)
                    Trace.endSection()
                }
            }
            launch {
                firstVisibleMonth.flatMapLatest {
                    getScheduleByYearMonthFlowUseCase(it.year, it.month.value)
                }.collect { schedules ->
                    Trace.beginSection("updateSchedules")
                    updateSchedules(schedules, firstVisibleMonth.value)
                    Trace.endSection()
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    getScheduleByYearMonthFlowUseCase(it.year, it.month.value)
                }.collect { schedules ->
                    Trace.beginSection("updateSchedules")
                    updateSchedules(schedules, lastVisibleMonth.value)
                    Trace.endSection()
                }
            }
            launch {
                firstVisibleMonth.flatMapLatest {
                    getMemorialByYearMonthFlowUseCase(it.year, it.month.value)
                }.collect { memorials ->
                    Trace.beginSection("updateMemorials")
                    updateMemorials(memorials, firstVisibleMonth.value)
                    Trace.endSection()
                }
            }
            launch {
                lastVisibleMonth.flatMapLatest {
                    getMemorialByYearMonthFlowUseCase(it.year, it.month.value)
                }.collect { memorials ->
                    Trace.beginSection("updateMemorials")
                    updateMemorials(memorials, lastVisibleMonth.value)
                    Trace.endSection()
                }
            }
        }
    }

    private fun updateTransactions(transactions: List<Transaction>, month: YearMonth) {
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
        val range = month.atStartOfMonth()..month.atEndOfMonth()
        calendarModelMap.asSequence().filter {
            it.key in range && it.key !in map.keys
        }.forEach {
            it.value.value = it.value.value.copy(
                transactions = emptyList(),
                income = BigDecimal(0),
                expense = BigDecimal(0),
                balance = BigDecimal(0)
            )
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

    private fun updateSchedules(schedules: List<Schedule>, month: YearMonth) {
        val map = mutableMapOf<LocalDate, MutableList<Schedule>>()
        schedules.forEach { schedule ->
            val time = schedule.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(schedule)
        }
        val range = month.atStartOfMonth()..month.atEndOfMonth()
        calendarModelMap.asSequence().filter {
            it.key in range && !map.containsKey(it.key)
        }.forEach {
            it.value.value = it.value.value.copy(schedules = emptyList())
        }
        map.forEach { (time, schedules) ->
            val calendarModelState = getCalendarModelState(time)
            calendarModelState.value = calendarModelState.value.copy(schedules = schedules)
        }
    }

    private fun updateMemorials(memorials: List<Memorial>, month: YearMonth) {
        val map = mutableMapOf<LocalDate, MutableList<Memorial>>()
        memorials.forEach { memorial ->
            val time = memorial.time.toInstant().atZone(zoneId).toLocalDate()
            val list = map.getOrPut(time) { mutableListOf() }
            list.add(memorial)
        }
        val range = month.atStartOfMonth()..month.atEndOfMonth()
        calendarModelMap.asSequence().filter {
            it.key in range && !map.containsKey(it.key)
        }.forEach {
            it.value.value = it.value.value.copy(memorials = emptyList())
        }
        map.forEach { (time, memorials) ->
            val calendarModelState = getCalendarModelState(time)
            calendarModelState.value = calendarModelState.value.copy(memorials = memorials)
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
            updateScheduleUseCase(schedule.copy(isfinish = checked))
        }
    }

}