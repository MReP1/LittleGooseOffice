package little.goose.account.ui.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.constants.AccountConstant.INCOME
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.constants.KEY_TIME
import little.goose.common.constants.KEY_TRANSACTION
import little.goose.common.utils.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val defaultTransaction
        get() = Transaction(
            time = savedStateHandle.get<Long>(KEY_TIME)?.let {
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
        WriteSuccess
    }

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    val transaction = savedStateHandle.getStateFlow(KEY_TRANSACTION, defaultTransaction)

    fun setTransaction(transaction: Transaction) {
        savedStateHandle[KEY_TRANSACTION] = transaction
    }

    fun writeDatabase(transaction: Transaction, isAgain: Boolean) {
        val tra = if (transaction.type == INCOME && transaction.money.signum() == -1) {
            transaction.copy(money = transaction.money.abs())
        } else if (transaction.type == EXPENSE && transaction.money.signum() == 1) {
            transaction.copy(money = transaction.money.negate())
        } else transaction
        viewModelScope.launch {
            if (tra.id == null) {
                accountRepository.insertTransaction(tra)
            } else {
                accountRepository.updateTransaction(tra)
            }
            if (!isAgain) {
                _event.emit(Event.WriteSuccess)
            } else {
                setTransaction(defaultTransaction)
            }
        }
    }

}