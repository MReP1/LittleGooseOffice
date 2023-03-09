package little.goose.account.ui.analysis

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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.account.ui.analysis.widget.TransactionAnalysisLineChartView
import little.goose.account.ui.component.TransactionPercentCircleChart
import little.goose.account.ui.component.TransactionPercentColumn
import little.goose.common.collections.CircularLinkList
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import java.util.*

data class TransactionAnalysisContentState(
    val percentsState: TransactionAnalysisPercentsState = TransactionAnalysisPercentsState(),
    val timeState: TransactionAnalysisTimeState = TransactionAnalysisTimeState()
)

data class TransactionAnalysisPercentsState(
    val expensePercents: List<TransactionPercent> = listOf(),
    val incomePercents: List<TransactionPercent> = listOf(),
    val balancePercents: List<TransactionBalance> = listOf()
)

data class TransactionAnalysisTimeState(
    val type: TransactionAnalysisViewModel.Type = TransactionAnalysisViewModel.Type.MONTH,
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
                    timeMoneys = state.timeState.timeExpenses,
                    transactionPercents = state.percentsState.expensePercents
                )
            }
            1 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    timeMoneys = state.timeState.timeIncomes,
                    transactionPercents = state.percentsState.incomePercents
                )
            }
            2 -> {
                TransactionAnalysisBalanceContent(
                    modifier = Modifier.fillMaxSize(),
                    type = state.timeState.type,
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
    type: TransactionAnalysisViewModel.Type,
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
            val colorScheme = MaterialTheme.colorScheme
            val context = LocalContext.current
            val lineChartView = remember { TransactionAnalysisLineChartView(context) }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                factory = { lineChartView }
            )
            Spacer(modifier = Modifier.height(12.dp))
            LaunchedEffect(timeMoneys) {
                lineChartView.bindData(
                    timeMoneys, TransactionAnalysisLineChartView.Type.Common,
                    colorScheme.primary.toArgb(), colorScheme.tertiary.toArgb()
                )
            }
            // FIXME ThreadLocal
            val calendar = remember { Calendar.getInstance() }
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
                                text = when (type) {
                                    TransactionAnalysisViewModel.Type.MONTH -> {
                                        calendar.time = transactionBalance.time
                                        calendar.getDate().toString()
                                    }
                                    TransactionAnalysisViewModel.Type.YEAR -> {
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

            val context = LocalContext.current
            val lineChartView = remember { TransactionAnalysisLineChartView(context) }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                factory = { lineChartView }
            )
            LaunchedEffect(timeMoneys) {
                lineChartView.bindData(
                    timeMoneys, TransactionAnalysisLineChartView.Type.Common,
                    colorScheme.primary.toArgb(), colorScheme.tertiary.toArgb()
                )
            }

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