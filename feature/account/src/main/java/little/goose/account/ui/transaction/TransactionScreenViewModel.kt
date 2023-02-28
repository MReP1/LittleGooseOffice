package little.goose.account.ui.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.AccountRepository
import little.goose.common.constants.KEY_TRANSACTION
import javax.inject.Inject

@HiltViewModel
class TransactionScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository
) : ViewModel() {

    enum class Event {
        WriteSuccess
    }

    val transaction = savedStateHandle.getStateFlow(KEY_TRANSACTION, Transaction())

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    fun setTransaction(transaction: Transaction) {
        savedStateHandle[KEY_TRANSACTION] = transaction
    }

    fun writeDatabase(transaction: Transaction) {
        if (transaction.id == null) {
            insertTransaction(transaction)
        } else {
            updateTransaction(transaction )
        }
    }

    private fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val id =  accountRepository.insertTransaction(transaction)
            setTransaction(transaction.copy(id = id))
            _event.emit(Event.WriteSuccess)
        }
    }

    private fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            accountRepository.updateTransaction(transaction)
            _event.emit(Event.WriteSuccess)
        }
    }

}