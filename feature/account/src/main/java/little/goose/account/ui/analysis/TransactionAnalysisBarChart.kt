package little.goose.account.ui.analysis

import android.icu.util.Calendar
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.chart.bar.BarChart
import little.goose.chart.bar.BarData
import little.goose.common.utils.TimeType
import little.goose.common.utils.toChineseMonth
import little.goose.common.utils.toChineseMonthDay
import java.math.BigDecimal
import java.util.Date
import kotlin.math.absoluteValue

@Composable
fun TransactionAnalysisBarChart(
    modifier: Modifier = Modifier,
    timeMoneys: List<TimeMoney>,
    timeType: AnalysisHelper.TimeType,
    moneyType: MoneyType,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, iconId: Int?, content: String?
    ) -> Unit
) {

    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (selectedData, onSelectedDataChange) = rememberSaveable(stateSaver = BarData.saver) {
            mutableStateOf(null)
        }

        val dataList = remember(timeMoneys) {
            val calendar = Calendar.getInstance()
            timeMoneys.map { timeMoney ->
                calendar.time = timeMoney.time
                BarData(
                    xText = calendar.get(Calendar.MONTH).toString(),
                    amount = timeMoney.money.toFloat().absoluteValue,
                    id = calendar.get(Calendar.DATE).toString()
                )
            }
        }

        BarChart(
            modifier = Modifier
                .fillMaxSize()
                .height(140.dp)
                .padding(horizontal = 16.dp),
            dataList = dataList,
            selectedData = selectedData,
            onSelectedDataChange = onSelectedDataChange
        )

        val timeMoney = remember(timeMoneys, selectedData) {
            if (selectedData == null) null else {
                val calendar = Calendar.getInstance()
                timeMoneys.find {
                    calendar.time = it.time
                    calendar.get(Calendar.DATE).toString() == selectedData.id
                }
            }
        }

        timeMoney?.let {
            TextButton(onClick = {
                val timeTypeTmp = when (timeType) {
                    AnalysisHelper.TimeType.MONTH -> TimeType.DATE
                    AnalysisHelper.TimeType.YEAR -> TimeType.YEAR_MONTH
                }
                onNavigateToTransactionExample(
                    timeMoney.time, timeTypeTmp, moneyType, null, null
                )
            }) {
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
                val text = if (timeMoney.money == BigDecimal(0)) noTransactionText else {
                    "$timeText ${typeText}了${moneyText}元"
                }
                Text(text = text)
            }
        } ?: run {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}