package little.goose.account.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.entities.Transaction

data class TransactionColumnState(
    val transactions: List<Transaction>,
    val isMultiSelecting: Boolean,
    val multiSelectedTransactions: Set<Transaction>,
    val onTransactionSelected: (item: Transaction, selected: Boolean) -> Unit,
    val selectAllTransactions: () -> Unit,
    val cancelMultiSelecting: () -> Unit,
    val deleteTransactions: (List<Transaction>) -> Unit
)

@Composable
fun TransactionColumn(
    modifier: Modifier,
    state: TransactionColumnState,
    onTransactionClick: (Transaction) -> Unit
) {
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
                Text(text = transaction.description)
            } else {
                TransactionCard(
                    transaction = transaction,
                    isMultiSelecting = state.isMultiSelecting,
                    selected = state.multiSelectedTransactions.contains(transaction),
                    onTransactionClick = onTransactionClick,
                    onSelectTransaction = state.onTransactionSelected
                )
            }
        }
    }
}