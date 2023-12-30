package little.goose.search.transaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.design.system.component.MovableActionButton
import little.goose.design.system.component.MovableActionButtonState
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.theme.GooseTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchTransactionContent(
    modifier: Modifier = Modifier,
    transactionColumnState: TransactionColumnState,
    onNavigateToTransactionScreen: (transactionId: Long) -> Unit
) {
    if (transactionColumnState.transactions.isNotEmpty()) {
        TransactionColumn(
            modifier = modifier.fillMaxSize(),
            state = transactionColumnState,
            onTransactionEdit = { transaction ->
                transaction.id?.run(onNavigateToTransactionScreen)
            }
        )
    }

    val deleteDialogState = remember { DeleteDialogState() }

    if (transactionColumnState.isMultiSelecting) {
        val buttonState = remember { MovableActionButtonState() }
        LaunchedEffect(buttonState) { buttonState.expend() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    if (WindowInsets.isImeVisible) {
                        WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                    } else {
                        BottomAppBarDefaults.windowInsets
                    }
                )
        ) {
            MovableActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                state = buttonState,
                mainButtonContent = {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                },
                onMainButtonClick = {
                    deleteDialogState.show(onConfirm = {
                        transactionColumnState.deleteTransactions(
                            transactionColumnState.multiSelectedTransactions.toList()
                        )
                    })
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

    DeleteDialog(state = deleteDialogState)
}

@Preview
@Composable
private fun PreviewSearchTransactionContent() = GooseTheme {
    SearchTransactionContent(
        modifier = Modifier.fillMaxSize(),
        transactionColumnState = TransactionColumnState(),
        onNavigateToTransactionScreen = {}
    )
}