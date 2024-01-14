package little.goose.account.ui

import android.icu.util.Calendar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.ui.component.AccountTitle
import little.goose.account.ui.component.AccountTitleState
import little.goose.account.ui.component.MonthSelector
import little.goose.account.ui.component.MonthSelectorState
import little.goose.account.ui.component.TransactionColumn
import little.goose.account.ui.component.TransactionColumnState
import little.goose.common.utils.TimeType
import little.goose.common.utils.calendar
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.progressWith
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import little.goose.design.system.component.dialog.TimeSelectorCenterDialog
import little.goose.design.system.component.dialog.rememberDialogState
import little.goose.design.system.theme.GooseTheme
import little.goose.ui.icon.PullToSearchIcon
import little.goose.ui.surface.PullSurface

@Composable
fun AccountHome(
    modifier: Modifier = Modifier,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState,
    transactionColumnState: TransactionColumnState,
    onNavigateToTransactionScreen: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAccountAnalysis: () -> Unit
) {
    PullSurface(
        modifier = modifier,
        onPull = onNavigateToSearch,
        backgroundContent = { progress ->
            PullToSearchIcon(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(48.dp)
                    .offset(y = 32.dp * (progress - 1F))
                    .scale(progress.coerceIn(0.75F, 1F))
                    .alpha(progress.coerceIn(0.75F, 1F)),
                progress = progress.progressWith(0.52F, 0F, 1F),
                contentDescription = "Search",
            )
        },
        content = {
            TransactionColumn(
                modifier = Modifier.fillMaxSize(),
                state = transactionColumnState,
                title = {
                    AccountTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(118.dp),
                        accountTitleState = accountTitleState,
                        onNavigateToAnalysis = onNavigateToAccountAnalysis
                    )
                },
                monthSelector = {
                    val selectorTimeDialogState = rememberDialogState()

                    MonthSelector(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        state = monthSelectorState,
                        shape = MaterialTheme.shapes.large,
                        onSelectTimeClick = { selectorTimeDialogState.show() }
                    )

                    TimeSelectorCenterDialog(
                        state = selectorTimeDialogState,
                        initTime = remember(monthSelectorState.year, monthSelectorState.month) {
                            calendar.apply {
                                clear()
                                setYear(monthSelectorState.year)
                                setMonth(monthSelectorState.month)
                            }.time
                        },
                        type = TimeType.YEAR_MONTH,
                        onConfirm = {
                            val cal = calendar.apply { time = it }
                            monthSelectorState.onTimeChange(cal.getYear(), cal.getMonth())
                        }
                    )
                },
                onTransactionEdit = { transaction ->
                    transaction.id?.run(onNavigateToTransactionScreen)
                }
            )
        }
    )
}

@Preview
@Composable
private fun PreviewAccountHome() = GooseTheme {
    val calendar = remember { Calendar.getInstance() }
    AccountHome(
        modifier = Modifier.fillMaxSize(),
        accountTitleState = AccountTitleState(),
        monthSelectorState = MonthSelectorState(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            onTimeChange = { _, _ -> }
        ),
        transactionColumnState = TransactionColumnState(),
        onNavigateToAccountAnalysis = {},
        onNavigateToSearch = {},
        onNavigateToTransactionScreen = {}
    )
}