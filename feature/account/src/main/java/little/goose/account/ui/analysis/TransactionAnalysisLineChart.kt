package little.goose.account.ui.analysis

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import little.goose.account.R
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.account.ui.analysis.widget.TransactionAnalysisLineChartView
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseMonth
import little.goose.common.utils.toChineseMonthDay
import little.goose.design.system.theme.AccountTheme
import java.math.BigDecimal
import java.util.Date

@Composable
fun TransactionAnalysisLineChart(
    modifier: Modifier,
    timeType: AnalysisHelper.TimeType,
    moneyType: MoneyType,
    timeMoneys: List<TimeMoney>,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, iconId: Int?, content: String?
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
                            timeMoney.time, timeTypeTmp, moneyType, null, null
                        )
                    }
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionAnalysisLineChart() = AccountTheme {
    TransactionAnalysisLineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        timeType = AnalysisHelper.TimeType.MONTH,
        moneyType = MoneyType.BALANCE,
        timeMoneys = listOf(TimeMoney(Date(), BigDecimal(100))),
        onNavigateToTransactionExample = { _, _, _, _, _ -> }
    )
}