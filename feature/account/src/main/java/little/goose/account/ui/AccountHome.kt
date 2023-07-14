package little.goose.account.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import little.goose.account.ui.component.AccountTitle
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.ui.surface.PullSurface

@Composable
fun AccountHome(
    modifier: Modifier = Modifier,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState,
    transactionColumnState: TransactionColumnState,
    onNavigateToTransactionScreen: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    PullSurface(
        modifier = modifier,
        onPull = onNavigateToSearch,
        backgroundContent = { progress ->
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(min(48.dp, 24.dp + 24.dp * progress))
                    .alpha(progress.coerceIn(0.62F, 1F))
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
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
                    onTransactionEdit = { transaction ->
                        transaction.id?.run(onNavigateToTransactionScreen)
                    }
                )
            }
        }
    )
}