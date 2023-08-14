package little.goose.search.transaction

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import little.goose.account.data.entities.Transaction
import little.goose.account.logic.DeleteTransactionsEventUseCase
import little.goose.account.logic.DeleteTransactionsUseCase
import little.goose.account.logic.SearchTransactionByMoneyFlowUseCase
import little.goose.account.logic.SearchTransactionByTextFlowUseCase
import little.goose.account.ui.component.TransactionColumnState
import javax.inject.Inject

@HiltViewModel
class SearchTransactionViewModel @Inject constructor(
    private val searchTransactionByMoneyFlowUseCase: SearchTransactionByMoneyFlowUseCase,
    private val searchTransactionByTextFlowUseCase: SearchTransactionByTextFlowUseCase,
    private val deleteTransactionsUseCase: DeleteTransactionsUseCase,
    deleteTransactionsEventUseCase: DeleteTransactionsEventUseCase
) : ViewModel() {

    private val multiSelectedTransactions = MutableStateFlow(emptySet<Transaction>())

    private val _searchTransactionState = MutableStateFlow<SearchTransactionState>(
        SearchTransactionState.Empty(::search)
    )
    val searchTransactionState = _searchTransactionState.asStateFlow()

    private val _searchTransactionEvent = MutableSharedFlow<SearchTransactionEvent>()
    val searchTransactionEvent = _searchTransactionEvent.asSharedFlow()

    private var searchingJob: Job? = null

    init {
        deleteTransactionsEventUseCase().onEach {
            _searchTransactionEvent.emit(SearchTransactionEvent.DeleteTransactions(it))
        }.launchIn(viewModelScope)
    }

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchTransactionState.value = SearchTransactionState.Empty(::search)
            return
        }
        _searchTransactionState.value = SearchTransactionState.Loading(::search)
        searchingJob?.cancel()
        searchingJob = combine(
            if (keyword.isDigitsOnly()) {
                searchTransactionByMoneyFlowUseCase(keyword)
            } else {
                searchTransactionByTextFlowUseCase(keyword)
            },
            multiSelectedTransactions
        ) { transactions, multiSelectedTransactions ->
            _searchTransactionState.value = if (transactions.isEmpty()) {
                SearchTransactionState.Empty(::search)
            } else {
                SearchTransactionState.Success(
                    data = TransactionColumnState(
                        transactions = transactions,
                        multiSelectedTransactions = multiSelectedTransactions,
                        isMultiSelecting = multiSelectedTransactions.isNotEmpty(),
                        onTransactionSelected = ::selectTransaction,
                        selectAllTransactions = ::selectAllTransaction,
                        cancelMultiSelecting = ::cancelTransactionsMultiSelecting,
                        deleteTransactions = ::deleteTransactions
                    ),
                    search = ::search
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun deleteTransactions(transactions: List<Transaction>) {
        viewModelScope.launch {
            deleteTransactionsUseCase(transactions)
            cancelTransactionsMultiSelecting()
        }
    }

    private fun selectTransaction(transaction: Transaction, selected: Boolean) {
        multiSelectedTransactions.value = multiSelectedTransactions.value.toMutableSet().apply {
            if (selected) add(transaction) else remove(transaction)
        }
    }

    private fun selectAllTransaction() {
        multiSelectedTransactions.value =
            (searchTransactionState.value as? SearchTransactionState.Success)
                ?.data?.transactions?.toSet() ?: return
    }

    private fun cancelTransactionsMultiSelecting() {
        multiSelectedTransactions.value = emptySet()
    }

}