package little.goose.account.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import little.goose.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.entities.Transaction

class AccountFragmentViewModel : ViewModel() {

    fun getCurMonthTransactionFlow() = AccountRepository.getTransactionCurrentMonthFlow()

    val deleteReceiver = DeleteItemBroadcastReceiver<Transaction>()

    suspend fun getTransactionByYearAndMonthFlow(year: Int, month: Int) =
        AccountRepository.getTransactionByYearMonthFlow(year, month).stateIn(viewModelScope)
}