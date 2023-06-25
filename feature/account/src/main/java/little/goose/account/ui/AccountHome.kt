package little.goose.account.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.component.AccountTitle
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState

@Composable
fun AccountHome(
    modifier: Modifier = Modifier,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState,
    transactionColumnState: TransactionColumnState,
    onNavigateToTransactionScreen: (Long) -> Unit
) {
    AccountScreen(
        modifier = modifier,
        transactionColumnState = transactionColumnState,
        accountTitleState = accountTitleState,
        onTransactionEdit = { transaction ->
            transaction.id?.run(onNavigateToTransactionScreen)
        },
        monthSelectorState = monthSelectorState
    )
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    transactionColumnState: TransactionColumnState,
    onTransactionEdit: (Transaction) -> Unit,
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
            onTransactionEdit = onTransactionEdit
        )
    }
}