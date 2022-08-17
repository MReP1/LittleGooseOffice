package little.goose.account.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import little.goose.account.common.dialog.time.TimeType
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.constant.MoneyType
import little.goose.account.logic.data.entities.Transaction
import little.goose.account.utils.*
import java.util.*

class TransactionExampleViewModel(
    private val time: Date,
    private val content: String?,
    private val timeType: TimeType,
    private val moneyType: MoneyType
) : ViewModel() {

    lateinit var transactions: Flow<List<Transaction>>

    var deleteTransaction = MutableStateFlow<Transaction?>(null)

    init {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.time = time

            transactions = if (content == null) {
                when (timeType) {
                    TimeType.DATE -> AccountRepository.getTransactionByDateFlow(
                        calendar.getYear(), calendar.getMonth(), calendar.getDate(), moneyType
                    )
                    TimeType.YEAR_MONTH -> AccountRepository.getTransactionByYearMonthFlow(
                        calendar.getYear(), calendar.getMonth(), moneyType
                    )
                    else -> {
                        AccountRepository.getAllTransactionFlow()
                    }
                }
            } else {
                when (timeType) {
                    TimeType.YEAR -> {
                        AccountRepository.getTransactionByYearFlowWithKeyContent(
                            calendar.getYear(), content
                        )
                    }
                    TimeType.YEAR_MONTH -> {
                        AccountRepository.getTransactionByYearMonthFlowWithKeyContent(
                            calendar.getYear(), calendar.getMonth(), content
                        )
                    }
                    else -> {
                        AccountRepository.getAllTransactionFlow()
                    }
                }
            }
        }
    }

}