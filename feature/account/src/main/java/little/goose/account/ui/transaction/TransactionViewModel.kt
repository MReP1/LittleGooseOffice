package little.goose.account.ui.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.GetTransactionByIdFlowUseCase
import little.goose.account.logic.InsertTransactionUseCase
import little.goose.account.logic.UpdateTransactionUseCase
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.setDate
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionByIdFlowUseCase: GetTransactionByIdFlowUseCase,
    private val insertTransactionUseCase: InsertTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase
) : ViewModel() {

    private val args = TransactionRouteArgs(savedStateHandle)

    private val defaultTransaction
        get() = Transaction(
            time = args.time?.let {
                val time = Calendar.getInstance().apply { timeInMillis = it }
                Calendar.getInstance().apply {
                    setYear(time.getYear())
                    setMonth(time.getMonth())
                    setDate(time.getDate())
                }.time
            } ?: Date(),
            icon_id = TransactionIconHelper.expenseIconList.first().id,
            content = TransactionIconHelper.expenseIconList.first().name
        )

    enum class Event {
        WriteSuccess,
        CantBeZero
    }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _transaction = MutableStateFlow(defaultTransaction)
    val transaction = _transaction.asStateFlow()

    init {
        viewModelScope.launch {
            args.transactionId?.let { id ->
                _transaction.value = getTransactionByIdFlowUseCase(id).first()
            }
        }
    }

    fun setTransaction(transaction: Transaction) {
        _transaction.value = transaction
    }

    fun writeDatabase(transaction: Transaction, isAgain: Boolean) {
        // 检查金额是否为空
        if (transaction.money == BigDecimal.ZERO) {
            viewModelScope.launch { _event.emit(Event.CantBeZero) }
            return
        }

        // 根据类型将金额调整为对应正负，支出对应负值，收入对应正值
        val tra = if (transaction.type == INCOME && transaction.money.signum() == -1) {
            transaction.copy(money = transaction.money.abs())
        } else if (transaction.type == EXPENSE && transaction.money.signum() == 1) {
            transaction.copy(money = transaction.money.negate())
        } else transaction

        viewModelScope.launch {
            // 写入数据库
            if (tra.id == null) {
                insertTransactionUseCase(tra)
            } else {
                updateTransactionUseCase(tra)
            }

            if (!isAgain) {
                // 若点击完成，则结束记账
                _event.emit(Event.WriteSuccess)
            } else {
                // 若点击下一笔，则需要重置Transaction
                setTransaction(defaultTransaction)
            }
        }
    }

}