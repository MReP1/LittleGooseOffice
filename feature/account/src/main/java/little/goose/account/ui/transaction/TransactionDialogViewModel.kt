package little.goose.account.ui.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.DeleteTransactionUseCase
import little.goose.account.logic.GetTransactionByIdFlowUseCase
import javax.inject.Inject

@HiltViewModel
class TransactionDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getTransactionByIdFlowUseCase: GetTransactionByIdFlowUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    val transaction = getTransactionByIdFlowUseCase(
        savedStateHandle.get<Long>(KEY_TRANSACTION_ID)!!
    ).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Transaction()
    )

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(NonCancellable) {
            deleteTransactionUseCase(transaction)
        }
    }

}