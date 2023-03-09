package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TransactionAnalysisScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    // FIXME 删除刷新
    val viewModel = hiltViewModel<TransactionAnalysisViewModel>()
    val bottomBarState by viewModel.bottomBarState.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    val contentState by viewModel.contentState.collectAsState()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)

    Scaffold(
        modifier = modifier,
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
        bottomBar = {
            TransactionAnalysisBottomBar(
                modifier = Modifier.wrapContentHeight(),
                state = bottomBarState
            )
        },
        content = {
            TransactionAnalysisContent(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                pagerState = pagerState,
                state = contentState
            )
        }
    )
}