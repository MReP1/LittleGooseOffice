package little.goose.account.ui

import androidx.compose.runtime.Stable
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumnState

@Stable
data class AccountHomeState(
    val accountTitleState: AccountTitleState = AccountTitleState(),
    val transactionColumnState: TransactionColumnState = TransactionColumnState(),
    val monthSelectorState: MonthSelectorState = MonthSelectorState()
)