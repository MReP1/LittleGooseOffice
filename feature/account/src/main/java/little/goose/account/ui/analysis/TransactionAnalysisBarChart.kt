package little.goose.account.ui.analysis

import android.icu.util.Calendar
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.chart.bar.BarChart
import little.goose.chart.bar.BarData
import little.goose.common.utils.TimeType
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
    val currentTimeMoney by rememberUpdatedState(newValue = timeMoneys)
    val colorScheme = MaterialTheme.colorScheme
    val dataList = remember(timeMoneys) {
        val calendar = Calendar.getInstance()
        timeMoneys.map { timeMoney ->
            calendar.time = timeMoney.time
            BarData(
                xText = calendar.get(Calendar.MONTH).toString(),
                amount = timeMoney.money.toFloat().absoluteValue,
                color = colorScheme.primary,
                id = calendar.get(Calendar.DATE).toString()
            )
        }
    }

    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (selectedData, onSelectedData) = remember {
            mutableStateOf<BarData?>(null)
        }

        BarChart(
            modifier = Modifier
                .fillMaxSize()
                .height(140.dp)
                .padding(horizontal = 16.dp),
            dataList = dataList,
            selectedData = selectedData,
            onSelectedDataChange = onSelectedData
        )

        selectedData?.let { barData ->
            TextButton(onClick = {
                val timeTypeTmp = when (timeType) {
                    AnalysisHelper.TimeType.MONTH -> TimeType.DATE
                    AnalysisHelper.TimeType.YEAR -> TimeType.YEAR_MONTH
                }
                currentTimeMoney.find {
                    val calendar = Calendar.getInstance()
                    calendar.time = it.time
                    calendar.get(Calendar.DATE).toString() == barData.id
                }?.let { timeMoney ->
                    onNavigateToTransactionExample(
                        timeMoney.time, timeTypeTmp, moneyType, null, null
                    )
                }
            }) {
                Text(text = barData.xText)
            }
        } ?: run {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}