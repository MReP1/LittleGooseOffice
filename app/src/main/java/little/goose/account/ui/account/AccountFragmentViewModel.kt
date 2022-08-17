package little.goose.account.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.AccountRepository
import little.goose.account.logic.data.entities.Transaction

class AccountFragmentViewModel : ViewModel() {

    suspend fun getCurMonthTransactionFlow() = AccountRepository.getTransactionCurrentMonthFlow()

    var deleteReceiver: DeleteItemBroadcastReceiver<Transaction>? = null

    suspend fun getTransactionByYearAndMonthFlow(year: Int, month: Int) =
        AccountRepository.getTransactionByYearMonthFlow(year, month).stateIn(viewModelScope)
}