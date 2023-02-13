package little.goose.account.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.account.data.entities.Transaction
import javax.inject.Inject

@HiltViewModel
class AccountFragmentViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    fun getCurMonthTransactionFlow() = accountRepository.getTransactionCurrentMonthFlow()

    val deleteReceiver =
        DeleteItemBroadcastReceiver<Transaction>()

    suspend fun getTransactionByYearAndMonthFlow(year: Int, month: Int) =
        accountRepository.getTransactionByYearMonthFlow(year, month).stateIn(viewModelScope)

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            accountRepository.addTransaction(transaction)
        }
    }

    fun addTransactions(transactions: List<Transaction>) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            accountRepository.addTransactions(transactions)
        }
    }

    fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            accountRepository.deleteTransactions(transactions)
        }
    }
}