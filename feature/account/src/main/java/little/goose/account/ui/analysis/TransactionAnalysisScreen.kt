package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import little.goose.account.data.constants.MoneyType
import little.goose.common.utils.TimeType
import little.goose.design.system.component.TimeSelector
import little.goose.design.system.component.TimeSelectorState
import java.util.Date

@Composable
fun TransactionAnalysisScreen(
    modifier: Modifier = Modifier,
    topBarState: TransactionAnalysisTopBarState,
    contentState: TransactionAnalysisContentState,
    bottomBarState: TransactionAnalysisBottomBarState,
    timeSelectorState: TimeSelectorState,
    onNavigateToTransactionExample: (
        time: Date, timeType: TimeType, moneyType: MoneyType, content: String?
    ) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 158.dp,
        topBar = {
            TransactionAnalysisTopBar(
                modifier = Modifier.fillMaxWidth(),
                state = topBarState,
                selectedTabIndex = pagerState.currentPage,
                onTabClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                onBack = onBack
            )
        },
        sheetContent = {
            TransactionAnalysisBottomBar(
                modifier = Modifier.wrapContentHeight(),
                state = bottomBarState,
                onSelectTimeClick = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                            scaffoldState.bottomSheetState.partialExpand()
                        } else {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                }
            )
            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                || scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
            ) {
                TimeSelector(
                    modifier = Modifier
                        .height(220.dp)
                        .align(Alignment.CenterHorizontally),
                    state = timeSelectorState,
                    timeType = when (bottomBarState.timeType) {
                        AnalysisHelper.TimeType.MONTH -> TimeType.YEAR_MONTH
                        AnalysisHelper.TimeType.YEAR -> TimeType.YEAR
                    },
                    isShowConfirm = false
                )
            } else {
                Spacer(modifier = Modifier.height(220.dp))
            }
        }
    ) { innerPadding ->
        TransactionAnalysisContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            pagerState = pagerState,
            state = contentState,
            onNavigateToTransactionExample = onNavigateToTransactionExample
        )
    }
}