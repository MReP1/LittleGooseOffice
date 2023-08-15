package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.common.utils.TimeType
import little.goose.common.utils.calendar
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.design.system.theme.AccountTheme
import java.math.BigDecimal
import java.util.Date

@Composable
fun TransactionAnalysisBalanceContent(
    modifier: Modifier,
    timeType: AnalysisHelper.TimeType,
    timeMoneys: List<TimeMoney>,
    transactionBalances: List<TransactionBalance>,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, iconId: Int?, content: String?
    ) -> Unit,
    onTransactionBalanceClick: (TransactionBalance) -> Unit
) {
    Surface(
        modifier = modifier
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionAnalysisLineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                timeType = timeType,
                moneyType = MoneyType.BALANCE,
                timeMoneys = timeMoneys,
                onNavigateToTransactionExample = onNavigateToTransactionExample
            )
            LazyColumn(modifier = Modifier.weight(1F)) {

                if (transactionBalances.isNotEmpty()) {
                    item {
                        TransactionBalanceTitle(modifier = Modifier)
                    }
                }

                items(
                    count = transactionBalances.size,
                ) { index ->
                    val transactionBalance = transactionBalances[index]
                    TransactionBalanceItem(
                        modifier = Modifier,
                        tonalElevation = if (index % 2 == 1) 3.dp else 1.dp,
                        timeType = timeType,
                        transactionBalance = transactionBalance,
                        onTransactionBalanceClick = onTransactionBalanceClick
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionBalanceTitle(
    modifier: Modifier
) {
    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.date),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.income),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.expense),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.balance),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TransactionBalanceItem(
    modifier: Modifier,
    tonalElevation: Dp,
    timeType: AnalysisHelper.TimeType,
    transactionBalance: TransactionBalance,
    onTransactionBalanceClick: (TransactionBalance) -> Unit
) {
    Surface(
        modifier = modifier.defaultMinSize(minHeight = 48.dp),
        tonalElevation = tonalElevation,
        onClick = { onTransactionBalanceClick(transactionBalance) },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (timeType) {
                    AnalysisHelper.TimeType.MONTH -> {
                        calendar.time = transactionBalance.time
                        calendar.getDate().toString()
                    }

                    AnalysisHelper.TimeType.YEAR -> {
                        calendar.time = transactionBalance.time
                        calendar.getMonth().toString()
                    }
                },
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
            Text(
                text = transactionBalance.income.toPlainString(),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
            Text(
                text = transactionBalance.expense.toPlainString(),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
            Text(
                text = transactionBalance.balance.toPlainString(),
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreviewTransactionBalanceTitle() = AccountTheme {
    TransactionBalanceTitle(modifier = Modifier)
}

@Preview
@Composable
fun PreviewTransactionBalanceItem() = AccountTheme {
    TransactionBalanceItem(
        modifier = Modifier,
        tonalElevation = 3.dp,
        timeType = AnalysisHelper.TimeType.MONTH,
        transactionBalance = TransactionBalance(
            time = Date(),
            expense = BigDecimal(12),
            income = BigDecimal(21),
            balance = BigDecimal(71)
        ),
        onTransactionBalanceClick = {}
    )
}