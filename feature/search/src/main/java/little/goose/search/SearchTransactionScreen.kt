package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.account.ui.transaction.TransactionDialog
import little.goose.account.ui.transaction.rememberTransactionDialogState

@Composable
internal fun SearchTransactionScreen(
    modifier: Modifier = Modifier,
    transactionColumnState: TransactionColumnState,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val transactionDialogState = rememberTransactionDialogState()

    TransactionDialog(
        state = transactionDialogState,
        onDelete = onDeleteTransaction
    )

    if (transactionColumnState.transactions.isNotEmpty()) {
        TransactionColumn(
            modifier = modifier.fillMaxSize(),
            state = transactionColumnState,
            onTransactionClick = {
                transactionDialogState.show(it)
            }
        )
    }
}