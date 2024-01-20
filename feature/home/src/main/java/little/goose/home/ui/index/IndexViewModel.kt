package little.goose.home.ui.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.daysOfWeek
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.logic.GetTransactionByDateFlowUseCase
import little.goose.common.utils.getRoundTwo
import little.goose.memorial.logic.GetMemorialByDateFlowUseCase
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val getTransactionByDateFlowUseCase: GetTransactionByDateFlowUseCase,
    private val getMemorialByDateFlowUseCase: GetMemorialByDateFlowUseCase,
) : ViewModel() {

    private val today = LocalDate.now()
    private val initMonth = YearMonth.now()
    private val startMonth = initMonth.minusMonths(120)
    private val endMonth = initMonth.plusMonths(120)
    private val daysOfWeek = daysOfWeek()

    private val currentDay: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())

    private val dayContentMap = mutableMapOf<LocalDate, StateFlow<IndexDayContent>>()

    private val indexHomeState = currentDay.map(::generateIndexHomeState).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        initialValue = generateIndexHomeState(currentDay.value)
    )

    private val indexTopBarState = currentDay.map(::generateIndexTopBarState).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        initialValue = generateIndexTopBarState(currentDay.value)
    )

    val indexState = combine(indexTopBarState, indexHomeState, ::IndexState).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000),
        initialValue = IndexState(indexTopBarState.value, indexHomeState.value)
    )

    private fun generateIndexTopBarState(date: LocalDate): IndexTopBarState {
        return IndexTopBarState(date, LocalDate.now(), ::updateCurrentDay)
    }

    private fun generateIndexHomeState(currentDay: LocalDate) = IndexHomeState(
        today, currentDay, initMonth,
        startMonth, endMonth, daysOfWeek,
        ::updateCurrentDay, ::getDayContentFlow
    )

    private fun updateCurrentDay(day: LocalDate) {
        currentDay.value = day
    }

    private fun getDayContentFlow(date: LocalDate): StateFlow<IndexDayContent> {
        return dayContentMap[date] ?: run {
            val year = date.year
            val month = date.month.value
            val day = date.dayOfMonth
            combine(
                getMemorialByDateFlowUseCase(year, month, day),
                getTransactionByDateFlowUseCase(year, month, day)
            ) { memorials, transactions ->
                var income = 0.0
                var expense = 0.0
                for (transaction in transactions) {
                    if (transaction.type == EXPENSE) {
                        expense += transaction.money.toDouble()
                    } else if (transaction.type == INCOME) {
                        income += transaction.money.toDouble()
                    }
                }
                IndexDayContent(
                    money = if (transactions.isEmpty()) null else {
                        BigDecimal(income + expense).getRoundTwo().toPlainString()
                    },
                    memorials = memorials,
                    transactions = transactions,
                    income = BigDecimal(income).getRoundTwo().toPlainString(),
                    expense = BigDecimal(expense).getRoundTwo().toPlainString()
                )
            }.flowOn(Dispatchers.Default).stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                IndexDayContent()
            ).also { dayContentMap[date] = it }
        }
    }

}