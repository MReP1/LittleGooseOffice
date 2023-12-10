package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.account.data.constants.MoneyType
import little.goose.account.data.models.TimeMoney
import little.goose.account.data.models.TransactionBalance
import little.goose.account.data.models.TransactionPercent
import little.goose.common.utils.TimeType
import little.goose.common.utils.setMonth
import little.goose.common.utils.setYear
import java.util.Calendar
import java.util.Date

data class TransactionAnalysisContentState(
    val year: Int,
    val month: Int,
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
        time: Date, timeType: TimeType, moneyType: MoneyType, iconId: Int?, content: String?
    ) -> Unit
) {

    fun clickTransactionPercent(moneyType: MoneyType, iconId: Int) {
        val dateMillis = Calendar.getInstance().apply {
            clear()
            setYear(state.year)
            setMonth(if (state.timeType == AnalysisHelper.TimeType.MONTH) state.month else 1)
        }.time
        val clickTimeType = when (state.timeType) {
            AnalysisHelper.TimeType.MONTH -> TimeType.YEAR_MONTH
            AnalysisHelper.TimeType.YEAR -> TimeType.YEAR
        }
        onNavigateToTransactionExample(
            dateMillis, clickTimeType, moneyType, iconId, null
        )
    }

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
                    onNavigateToTransactionExample = onNavigateToTransactionExample,
                    onTransactionPercentClick = {
                        clickTransactionPercent(MoneyType.EXPENSE, it.icon_id)
                    }
                )
            }

            1 -> {
                TransactionAnalysisCommonContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    moneyType = MoneyType.INCOME,
                    timeMoneys = state.timeState.timeIncomes,
                    transactionPercents = state.percentsState.incomePercents,
                    onNavigateToTransactionExample = onNavigateToTransactionExample,
                    onTransactionPercentClick = {
                        clickTransactionPercent(MoneyType.INCOME, it.icon_id)
                    }
                )
            }

            2 -> {
                TransactionAnalysisBalanceContent(
                    modifier = Modifier.fillMaxSize(),
                    timeType = state.timeType,
                    transactionBalances = state.percentsState.balancePercents,
                    onTransactionBalanceClick = {
                        val clickTimeType = when (state.timeType) {
                            AnalysisHelper.TimeType.MONTH -> TimeType.DATE
                            AnalysisHelper.TimeType.YEAR -> TimeType.YEAR_MONTH
                        }
                        onNavigateToTransactionExample(
                            it.time, clickTimeType, MoneyType.BALANCE, null, null
                        )
                    }
                )
            }
        }
    }
}