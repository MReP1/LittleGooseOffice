package little.goose.account.ui.analysis

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

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