package little.goose.account.ui.analysis

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.YearSelectorState
import little.goose.common.utils.calendar
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import little.goose.design.system.component.TimeSelectorState
import java.util.Calendar
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TransactionAnalysisViewModel @Inject constructor(
    private val analysisHelper: AnalysisHelper
) : ViewModel() {

    enum class TimeType { MONTH, YEAR }

    private val _timeType = MutableStateFlow(TimeType.MONTH)
    private val timeType = _timeType.asStateFlow()

    private val _year = MutableStateFlow(Calendar.getInstance().getYear())
    val year = _year.asStateFlow()

    private val _month = MutableStateFlow(Calendar.getInstance().getMonth())
    val month = _month.asStateFlow()

    val timeSelectorState = TimeSelectorState(
        calendar.apply { clear(); setYear(year.value); setMonth(month.value) }.time
    )


    init {
        viewModelScope.launch {
            combine(timeType, year, month) { type, year, month ->
                updateData(type, year, month)
                timeSelectorState.year = year
                timeSelectorState.month = month
            }.launchIn(this)

            combine(
                snapshotFlow { timeSelectorState.year },
                snapshotFlow { timeSelectorState.month }
            ) { year, month ->
                Pair(year, month)
            }.debounce(600L).onEach { (year, month) ->
                when (timeType.value) {
                    TimeType.MONTH -> changeTime(year, month)
                    TimeType.YEAR -> changeYear(year)
                }
            }.launchIn(this)
        }

    }

    fun updateData() {
        viewModelScope.launch { updateData(timeType.value, year.value, month.value) }
    }

    private suspend fun updateData(type: TimeType, year: Int, month: Int) {
        when (type) {
            TimeType.MONTH -> analysisHelper.updateTransactionListMonth(year, month)
            TimeType.YEAR -> analysisHelper.updateTransactionListYear(year)
        }
    }

    val contentState = combine(
        timeType,
        combine(
            analysisHelper.expensePercents,
            analysisHelper.incomePercents,
            analysisHelper.balances
        ) { expensePercents, incomePercents, balancePercents ->
            TransactionAnalysisPercentsState(expensePercents, incomePercents, balancePercents)
        },
        combine(
            analysisHelper.timeExpenses,
            analysisHelper.timeIncomes,
            analysisHelper.timeBalances
        ) { timeExpenses, timeIncomes, timeBalances ->
            TransactionAnalysisTimeState(timeExpenses, timeIncomes, timeBalances)
        }
    ) { timeType, percentsState, timeState ->
        TransactionAnalysisContentState(timeType, percentsState, timeState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TransactionAnalysisContentState()
    )

    val topBarState = combine(
        analysisHelper.expenseSum,
        analysisHelper.incomeSum,
        analysisHelper.balance
    ) { expenseSum, incomeSum, balance ->
        TransactionAnalysisTopBarState(expenseSum, incomeSum, balance)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionAnalysisTopBarState()
    )

    private val changeYear: (year: Int) -> Unit = { year -> _year.value = year }

    private val changeTime: (year: Int, month: Int) -> Unit = { year, month ->
        _year.value = year; _month.value = month
    }

    val bottomBarState = combine(timeType, year, month) { type, year, month ->
        TransactionAnalysisBottomBarState(
            type, year, month,
            MonthSelectorState(year, month, changeTime),
            YearSelectorState(year, changeYear),
            onTypeChange = { t -> _timeType.value = t }
        )
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionAnalysisBottomBarState(
            timeType.value, year.value, month.value,
            MonthSelectorState(year.value, month.value, changeTime),
            YearSelectorState(year.value, changeYear),
            onTypeChange = { t -> _timeType.value = t }
        )
    )

}