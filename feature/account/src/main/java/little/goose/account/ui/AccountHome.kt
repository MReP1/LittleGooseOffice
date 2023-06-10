package little.goose.account.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.*
import little.goose.account.ui.transaction.TransactionDialog
import little.goose.account.ui.transaction.rememberTransactionDialogState

@Composable
fun AccountHome(
    modifier: Modifier = Modifier,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState,
    transactionColumnState: TransactionColumnState,
    onNavigateToTransaction: (Long) -> Unit,
    deleteTransaction: (Transaction) -> Unit
) {
    val transactionDialogState = rememberTransactionDialogState()

    AccountScreen(
        modifier = modifier,
        transactionColumnState = transactionColumnState,
        accountTitleState = accountTitleState,
        onTransactionClick = transactionDialogState::show,
        monthSelectorState = monthSelectorState,
    )

    TransactionDialog(
        state = transactionDialogState,
        onNavigateToTransaction = onNavigateToTransaction,
        onDelete = deleteTransaction
    )
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    transactionColumnState: TransactionColumnState,
    onTransactionClick: (Transaction) -> Unit,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState
) {
    Column(modifier) {
        AccountTitle(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            accountTitleState = accountTitleState,
            monthSelectorState = monthSelectorState
        )
        TransactionColumn(
            modifier = Modifier.weight(1F),
            state = transactionColumnState,
            onTransactionClick = onTransactionClick
        )
    }
}