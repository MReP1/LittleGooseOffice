package little.goose.account.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.YearSelectorState
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TransactionAnalysisViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val analysisHelper = AnalysisHelper(accountRepository)

    enum class Type { MONTH, YEAR }

    private val _type = MutableStateFlow(Type.MONTH)
    val type = _type.asStateFlow()

    private val _year = MutableStateFlow(Calendar.getInstance().getYear())
    val year = _year.asStateFlow()

    private val _month = MutableStateFlow(Calendar.getInstance().getMonth())
    val month = _month.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            combine(type, year, month) { type, year, month ->
                when (type) {
                    Type.MONTH -> analysisHelper.updateTransactionListMonth(year, month)
                    Type.YEAR -> analysisHelper.updateTransactionListYear(year)
                }
            }.collect()
        }
    }

    val expensePercents get() = analysisHelper.expensePercents

    private val incomePercents get() = analysisHelper.incomePercents

    private val balances get() = analysisHelper.balances

    private val expenseSum get() = analysisHelper.expenseSum

    private val incomeSum get() = analysisHelper.incomeSum

    private val balance get() = analysisHelper.balance

    private val timeExpenses get() = analysisHelper.timeExpenses

    private val timeIncomes get() = analysisHelper.timeIncomes

    private val timeBalances get() = analysisHelper.timeBalances

    val bottomBarState = combine(type, year, month) { type, year, month ->
        TransactionAnalysisBottomBarState(
            type, year, month,
            MonthSelectorState(year, month) { y, m -> _year.value = y; _month.value = m },
            YearSelectorState(year) { y -> _year.value = y },
            onTypeChange = { t -> _type.value = t }
        )
    }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionAnalysisBottomBarState(
            type.value, year.value, month.value,
            MonthSelectorState(year.value, month.value) { y, m ->
                _year.value = y; _month.value = m
            },
            YearSelectorState(year.value) { y -> _year.value = y },
            onTypeChange = { t -> _type.value = t }
        )
    )

}