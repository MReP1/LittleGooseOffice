package little.goose.search.transaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.account.ui.transaction.TransactionDialog
import little.goose.account.ui.transaction.rememberTransactionDialogState
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState

@Composable
internal fun SearchTransactionContent(
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

    if (transactionColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    transactionColumnState.deleteTransactions(
                        transactionColumnState.multiSelectedTransactions.toList()
                    )
                },
                topSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.DoneAll, contentDescription = "SelectAll")
                },
                onTopSubButtonClick = {
                    transactionColumnState.selectAllTransactions()
                },
                bottomSubButtonContent = {
                    Icon(imageVector = Icons.Rounded.RemoveDone, contentDescription = "RemoveDone")
                },
                onBottomSubButtonClick = {
                    transactionColumnState.cancelMultiSelecting()
                }
            )
        }
    }
}