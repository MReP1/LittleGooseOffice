package little.goose.account.ui.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.account.ui.TransactionExampleActivity
import little.goose.account.ui.analysis.widget.TransactionAnalysisLineChartView
import little.goose.account.ui.component.TransactionPercentCircleChart
import little.goose.account.ui.component.TransactionPercentColumn
import little.goose.common.collections.CircularLinkList
import little.goose.common.dialog.time.TimeType
import little.goose.common.utils.*
import java.math.BigDecimal
import java.util.*

data class TransactionAnalysisContentState(
    val timeType: TransactionAnalysisViewModel.TimeType = TransactionAnalysisViewModel.TimeType.MONTH,
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransactionAnalysisContent(
    modifier: Modifier = Modifier,
    state: TransactionAnalysisContentState,
    pagerState: PagerState
) {
    HorizontalPager(
        count = 3,
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
                    transactionPercents = state.percentsState.expensePercents
                )
            }
            1 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    moneyType = MoneyType.INCOME,
                    timeMoneys = state.timeState.timeIncomes,
                    transactionPercents = state.percentsState.incomePercents
                )
            }
            2 -> {
                TransactionAnalysisBalanceContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    timeMoneys = state.timeState.balances,
                    transactionBalances = state.percentsState.balancePercents
                )
            }
        }
    }
}

@Composable
fun TransactionAnalysisBalanceContent(
    modifier: Modifier,
    timeType: TransactionAnalysisViewModel.TimeType,
    timeMoneys: List<TimeMoney>,
    transactionBalances: List<TransactionBalance>
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
                timeMoneys = timeMoneys
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
                                text = "日期",
                                modifier = Modifier.weight(1F),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "收入", modifier = Modifier.weight(1F),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "支出", modifier = Modifier.weight(1F),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "结余", modifier = Modifier.weight(1F),
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
                                    TransactionAnalysisViewModel.TimeType.MONTH -> {
                                        calendar.time = transactionBalance.time
                                        calendar.getDate().toString()
                                    }
                                    TransactionAnalysisViewModel.TimeType.YEAR -> {
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
    timeType: TransactionAnalysisViewModel.TimeType,
    moneyType: MoneyType,
    timeMoneys: List<TimeMoney>,
    transactionPercents: List<TransactionPercent>
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
                timeMoneys = timeMoneys
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
    timeType: TransactionAnalysisViewModel.TimeType,
    moneyType: MoneyType,
    timeMoneys: List<TimeMoney>
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
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(horizontal = 16.dp),
            factory = { lineChartView }
        ) {
            lineChartView.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(entry: Entry?, highlight: Highlight?) {
                    if (entry == null || highlight == null) return
                    currentTimeMoney = entry.data as TimeMoney
                }

                override fun onNothingSelected() {
                    currentTimeMoney = null
                }
            })
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
                    TransactionAnalysisViewModel.TimeType.MONTH -> timeMoney.time.toChineseMonthDay()
                    TransactionAnalysisViewModel.TimeType.YEAR -> timeMoney.time.toChineseMonth()
                }
                val typeText = if (timeMoney.money.signum() < 0) "消费" else "收入"
                val moneyText = timeMoney.money.abs().toPlainString()
                val noTransactionText = "没有记账"
                if (timeMoney.money == BigDecimal(0)) noTransactionText else {
                    "$timeText ${typeText}了${moneyText}元"
                }
            } ?: "",
            modifier = Modifier
                .height(26.dp)
                .clickable {
                    currentTimeMoney?.let { timeMoney ->
                        val timeTypeTmp = when (timeType) {
                            TransactionAnalysisViewModel.TimeType.MONTH -> TimeType.DATE
                            TransactionAnalysisViewModel.TimeType.YEAR -> TimeType.YEAR_MONTH
                        }
                        TransactionExampleActivity.open(
                            context, timeMoney.time, timeTypeTmp, moneyType
                        )
                    }
                }
        )
    }
}