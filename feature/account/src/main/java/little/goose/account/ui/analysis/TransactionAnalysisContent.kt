package little.goose.account.ui.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import little.goose.account.R
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.account.ui.analysis.widget.TransactionAnalysisLineChartView
import little.goose.account.ui.component.TransactionPercentCircleChart
import little.goose.account.ui.component.TransactionPercentColumn
import little.goose.common.collections.CircularLinkList
import little.goose.common.utils.TimeType
import little.goose.common.utils.calendar
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.toChineseMonth
import little.goose.common.utils.toChineseMonthDay
import java.math.BigDecimal
import java.util.Date

data class TransactionAnalysisContentState(
    val timeType: AnalysisHelper.TimeType = AnalysisHelper.TimeType.MONTH,
    val percentsState: TransactionAnalysisPercentsState = TransactionAnalysisPercentsState(),
    val timeState: TransactionAnalysisTimeState = TransactionAnalysisTimeState()
)

data class TransactionAnalysisPercentsState(
    val expensePercents: List<TransactionPercent> = listOf(),
    val incomePercents: List<TransactionPercent> = listOf(),
    val balancePercents: List<TransactionBalance> = listOf()
)

data class TransactionAnalysisTimeState(
    val timeExpenses: List<TimeMoney> = listOf(),
    val timeIncomes: List<TimeMoney> = listOf(),
    val balances: List<TimeMoney> = listOf()
)

@Composable
fun TransactionAnalysisContent(
    modifier: Modifier = Modifier,
    state: TransactionAnalysisContentState,
    pagerState: PagerState,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit
) {
    HorizontalPager(
        modifier = modifier
            .fillMaxSize(),
        state = pagerState
    ) { index ->
        when (index) {
            0 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    moneyType = MoneyType.EXPENSE,
                    timeMoneys = state.timeState.timeExpenses,
                    transactionPercents = state.percentsState.expensePercents,
                    onNavigateToTransactionExample = onNavigateToTransactionExample
                )
            }

            1 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    moneyType = MoneyType.INCOME,
                    timeMoneys = state.timeState.timeIncomes,
                    transactionPercents = state.percentsState.incomePercents,
                    onNavigateToTransactionExample = onNavigateToTransactionExample
                )
            }

            2 -> {
                TransactionAnalysisBalanceContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    timeMoneys = state.timeState.balances,
                    transactionBalances = state.percentsState.balancePercents,
                    onNavigateToTransactionExample = onNavigateToTransactionExample
                )
            }
        }
    }
}

@Composable
fun TransactionAnalysisBalanceContent(
    modifier: Modifier,
    timeType: AnalysisHelper.TimeType,
    timeMoneys: List<TimeMoney>,
    transactionBalances: List<TransactionBalance>,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit
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
                item {
                    Surface(tonalElevation = 3.dp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                items(
                    count = transactionBalances.size,
                ) { index ->
                    val transactionBalance = transactionBalances[index]
                    Surface(tonalElevation = if (index % 2 == 1) 3.dp else 1.dp) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
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
            }
        }
    }
}

@Composable
fun TransactionAnalysisCommonContent(
    modifier: Modifier,
    timeType: AnalysisHelper.TimeType,
    moneyType: MoneyType,
    timeMoneys: List<TimeMoney>,
    transactionPercents: List<TransactionPercent>,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit
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
            val colorScheme = MaterialTheme.colorScheme
            val colors = remember {
                CircularLinkList<Color>().apply {
                    add(colorScheme.primaryContainer)
                    add(colorScheme.errorContainer)
                    add(colorScheme.secondaryContainer)
                    add(colorScheme.tertiaryContainer)
                }
            }
            val trColors = remember(transactionPercents, colors) {
                List(transactionPercents.size) { index ->
                    var backgroundColor = colors.next()
                    if (index == transactionPercents.lastIndex
                        && backgroundColor == colorScheme.errorContainer
                    ) {
                        colors.next()
                        backgroundColor = colors.next()
                    }
                    backgroundColor to colorScheme.contentColorFor(backgroundColor)
                }
            }

            TransactionAnalysisLineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                timeType = timeType,
                moneyType = moneyType,
                timeMoneys = timeMoneys,
                onNavigateToTransactionExample = onNavigateToTransactionExample
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransactionPercentCircleChart(
                modifier = Modifier.size(200.dp),
                transactionPercents = transactionPercents,
                colors = trColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            TransactionPercentColumn(
                modifier = Modifier.wrapContentSize(),
                transactionPercents = transactionPercents,
                colors = trColors
            )
        }
    }
}

@Composable
fun TransactionAnalysisLineChart(
    modifier: Modifier,
    timeType: AnalysisHelper.TimeType,
    moneyType: MoneyType,
    timeMoneys: List<TimeMoney>,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var currentTimeMoney: TimeMoney? by remember(timeMoneys, moneyType, timeType) {
            mutableStateOf(null)
        }
        val lineChartView = remember { TransactionAnalysisLineChartView(context) }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { lineChartView }
            ) {
                lineChartView.setOnChartValueSelectedListener(object :
                    OnChartValueSelectedListener {
                    override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
                        if (entry == null || highlight == null) return
                        currentTimeMoney = entry.data as TimeMoney
                    }

                    override fun onNothingSelected() {
                        currentTimeMoney = null
                    }
                })
            }
        }
        LaunchedEffect(timeMoneys) {
            lineChartView.bindData(
                timeMoneys, TransactionAnalysisLineChartView.Type.Common,
                colorScheme.primary.toArgb(), colorScheme.tertiary.toArgb()
            )
        }
        Text(
            text = currentTimeMoney?.let { timeMoney ->
                val timeText = when (timeType) {
                    AnalysisHelper.TimeType.MONTH -> timeMoney.time.toChineseMonthDay()
                    AnalysisHelper.TimeType.YEAR -> timeMoney.time.toChineseMonth()
                }
                val typeText = if (timeMoney.money.signum() < 0) {
                    stringResource(id = R.string.expense)
                } else {
                    stringResource(id = R.string.income)
                }
                val moneyText = timeMoney.money.abs().toPlainString()
                val noTransactionText = stringResource(id = R.string.no_transaction)
                if (timeMoney.money == BigDecimal(0)) noTransactionText else {
                    "$timeText ${typeText}了${moneyText}元"
                }
            } ?: "",
            modifier = Modifier
                .height(26.dp)
                .clickable {
                    currentTimeMoney?.let { timeMoney ->
                        val timeTypeTmp = when (timeType) {
                            AnalysisHelper.TimeType.MONTH -> TimeType.DATE
                            AnalysisHelper.TimeType.YEAR -> TimeType.YEAR_MONTH
                        }
                        onNavigateToTransactionExample(
                            timeMoney.time, timeTypeTmp, moneyType, null
                        )
                    }
                }
        )
    }
}