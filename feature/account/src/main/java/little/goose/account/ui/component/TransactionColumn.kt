package little.goose.account.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.entities.Transaction
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState

data class TransactionColumnState(
    val transactions: List<Transaction> = emptyList(),
    val isMultiSelecting: Boolean = false,
    val multiSelectedTransactions: Set<Transaction> = emptySet(),
    val onTransactionSelected: (item: Transaction, selected: Boolean) -> Unit = { _, _ -> },
    val selectAllTransactions: () -> Unit = {},
    val cancelMultiSelecting: () -> Unit = {},
    val deleteTransactions: (List<Transaction>) -> Unit = {}
)

@Composable
fun TransactionColumn(
    modifier: Modifier,
    state: TransactionColumnState,
    onTransactionEdit: (Transaction) -> Unit
) {
    val deleteDialogState = remember { DeleteDialogState() }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = state.transactions,
            key = { it.id ?: it }
        ) { transaction ->
            if (transaction.type == AccountConstant.TIME) {
                Text(
                    text = transaction.description,
                    modifier = Modifier
                )
            } else {
                var isExpended by remember { mutableStateOf(false) }
                TransactionCard(
                    modifier = Modifier,
                    transaction = transaction,
                    isExpended = isExpended,
                    isMultiSelecting = state.isMultiSelecting,
                    selected = state.multiSelectedTransactions.contains(transaction),
                    onTransactionClick = { isExpended = !isExpended },
                    onTransactionEdit = onTransactionEdit,
                    onTransactionDelete = {
                        deleteDialogState.show {
                            state.deleteTransactions(listOf(it))
                        }
                    },
                    onSelectTransaction = state.onTransactionSelected
                )
            }
        }
    }

    DeleteDialog(state = deleteDialogState)
}