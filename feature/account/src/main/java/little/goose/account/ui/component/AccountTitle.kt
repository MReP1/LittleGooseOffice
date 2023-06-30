package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.common.utils.TimeType
import little.goose.common.utils.calendar
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import little.goose.design.system.component.dialog.TimeSelectorCenterDialog
import little.goose.design.system.component.dialog.rememberDialogState
import java.math.BigDecimal

@Stable
data class AccountTitleState(
    val curMonthExpenseSum: BigDecimal = BigDecimal(0),
    val curMonthIncomeSum: BigDecimal = BigDecimal(0),
    val curMonthBalance: BigDecimal = BigDecimal(0),
    val totalExpenseSum: BigDecimal = BigDecimal(0),
    val totalIncomeSum: BigDecimal = BigDecimal(0),
    val totalBalance: BigDecimal = BigDecimal(0),
)

@Composable
fun AccountTitle(
    modifier: Modifier = Modifier,
    accountTitleState: AccountTitleState,
    monthSelectorState: MonthSelectorState,
) {
    var showTotal by remember { mutableStateOf(false) }

    val incomeMoney = remember(showTotal, accountTitleState) {
        if (showTotal) accountTitleState.totalIncomeSum.toPlainString()
        else accountTitleState.curMonthIncomeSum.toPlainString()
    }

    val expenseMoney = remember(showTotal, accountTitleState) {
        if (showTotal) accountTitleState.totalExpenseSum.toPlainString()
        else accountTitleState.curMonthExpenseSum.toPlainString()
    }

    val balanceMoney = remember(showTotal, accountTitleState) {
        if (showTotal) accountTitleState.totalBalance.toPlainString()
        else accountTitleState.curMonthBalance.toPlainString()
    }

    Card(
        onClick = { showTotal = !showTotal },
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        id = if (showTotal) R.string.total_income
                        else R.string.current_month_income
                    )
                )
                Text(text = incomeMoney)
            }
            Column(
                modifier = Modifier.weight(1F),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(
                        id = if (showTotal) R.string.total_expense
                        else R.string.current_month_expense
                    )
                )
                Text(text = expenseMoney)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    id = if (showTotal) R.string.total_balance
                    else R.string.current_month_balance
                )
            )
            Text(text = balanceMoney, style = MaterialTheme.typography.displaySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        val selectorTimeDialogState = rememberDialogState()

        MonthSelector(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            state = monthSelectorState,
            shape = RectangleShape,
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
    }
}

@Preview(widthDp = 375, heightDp = 200)
@Composable
fun PreviewAccountTitle() {
    AccountTitle(
        modifier = Modifier.wrapContentSize(),
        accountTitleState = AccountTitleState(
            BigDecimal(5125), BigDecimal(1234), BigDecimal(8512),
            BigDecimal(23), BigDecimal(512293), BigDecimal(2144)
        ),
        MonthSelectorState(2000, 12) { _, _ -> }
    )
}