package little.goose.account.ui.analysis

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.YearSelectorState
import little.goose.common.utils.calendar
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import little.goose.design.system.component.TimeSelectorState
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TransactionAnalysisViewModel @Inject constructor(
    private val analysisHelper: AnalysisHelper
) : ViewModel() {

    val timeSelectorState = TimeSelectorState(
        calendar.apply {
            clear()
            setYear(analysisHelper.year.value)
            setMonth(analysisHelper.month.value)
        }.time
    )

    init {
        viewModelScope.launch {
            combine(
                snapshotFlow { timeSelectorState.year },
                snapshotFlow { timeSelectorState.month }
            ) { year, month ->
                Pair(year, month)
            }.debounce(600L).collect { (year, month) ->
                when (analysisHelper.timeType.value) {
                    AnalysisHelper.TimeType.MONTH -> analysisHelper.changeTime(year, month)
                    AnalysisHelper.TimeType.YEAR -> analysisHelper.changeYear(year)
                }
            }
        }

        analysisHelper.bindCoroutineScope(viewModelScope)
    }

    val contentState = combine(
        analysisHelper.year,
        analysisHelper.month,
        analysisHelper.timeType,
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
    ) { year, month, timeType, percentsState, timeState ->
        TransactionAnalysisContentState(year, month, timeType, percentsState, timeState)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TransactionAnalysisContentState(
            analysisHelper.year.value,
            analysisHelper.month.value
        )
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

    val bottomBarState = combine(
        analysisHelper.timeType,
        analysisHelper.year,
        analysisHelper.month
    ) { type, year, month ->
        TransactionAnalysisBottomBarState(
            type, year, month,
            MonthSelectorState(year, month, analysisHelper::changeTime),
            YearSelectorState(year, analysisHelper::changeYear),
            onTypeChange = analysisHelper::changeTimeType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionAnalysisBottomBarState(
            analysisHelper.timeType.value, analysisHelper.year.value, analysisHelper.month.value,
            MonthSelectorState(
                analysisHelper.year.value, analysisHelper.month.value, analysisHelper::changeTime
            ),
            YearSelectorState(analysisHelper.year.value, analysisHelper::changeYear),
            onTypeChange = analysisHelper::changeTimeType
        )
    )

}