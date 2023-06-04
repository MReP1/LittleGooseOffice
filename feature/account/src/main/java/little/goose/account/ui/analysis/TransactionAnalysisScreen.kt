package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.launch
import little.goose.common.utils.TimeType
import little.goose.design.system.component.TimeSelector

@Composable
fun TransactionAnalysisScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<TransactionAnalysisViewModel>()
    val bottomBarState by viewModel.bottomBarState.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    val contentState by viewModel.contentState.collectAsState()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val observer = object : DefaultLifecycleObserver {
            var isFirstTime = true
            override fun onStart(owner: LifecycleOwner) {
                if (!isFirstTime) {
                    viewModel.updateData()
                } else {
                    isFirstTime = false
                }
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        onDispose { lifecycle.lifecycle.removeObserver(observer) }
    }

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
                modifier = Modifier.height(100.dp),
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
                    state = viewModel.timeSelectorState,
                    timeType = when (bottomBarState.timeType) {
                        TransactionAnalysisViewModel.TimeType.MONTH -> TimeType.YEAR_MONTH
                        TransactionAnalysisViewModel.TimeType.YEAR -> TimeType.YEAR
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
            state = contentState
        )
    }
}