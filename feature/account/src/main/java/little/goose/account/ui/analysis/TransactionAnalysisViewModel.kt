package little.goose.account.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.YearSelectorState
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.Calendar
import javax.inject.Inject

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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(timeType, year, month) { type, year, month ->
                updateData(type, year, month)
            }.collect()
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

    val bottomBarState = combine(timeType, year, month) { type, year, month ->
        TransactionAnalysisBottomBarState(
            type, year, month,
            MonthSelectorState(year, month) { y, m -> _year.value = y; _month.value = m },
            YearSelectorState(year) { y -> _year.value = y },
            onTypeChange = { t -> _timeType.value = t }
        )
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionAnalysisBottomBarState(
            timeType.value, year.value, month.value,
            MonthSelectorState(year.value, month.value) { y, m ->
                _year.value = y; _month.value = m
            },
            YearSelectorState(year.value) { y -> _year.value = y },
            onTypeChange = { t -> _timeType.value = t }
        )
    )

}