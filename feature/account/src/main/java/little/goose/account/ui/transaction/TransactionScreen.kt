package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.component.TransactionEditSurfaceState
import little.goose.design.system.theme.AccountTheme

internal data class TransactionScreenState(
    val topBarState: TransactionScreenTopBarState = TransactionScreenTopBarState(),
    val editSurfaceState: TransactionEditSurfaceState = TransactionEditSurfaceState(),
    val iconPagerState: TransactionScreenIconPagerState = TransactionScreenIconPagerState(),
)

@Composable
internal fun TransactionScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit,
    transactionScreenState: TransactionScreenState,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.fillMaxWidth()
            ) {
                Snackbar(snackbarData = it)
            }
        },
        topBar = {
            TransactionScreenTopBar(
                modifier = Modifier.fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
                onTabSelected = onTabSelected,
                state = transactionScreenState.topBarState,
                onBack = onBack
            )
        },
        content = {
            TransactionScreenIconPager(
                modifier = Modifier.padding(it),
                pagerState = pagerState,
                state = transactionScreenState.iconPagerState,
            )
        },
        bottomBar = {
            TransactionEditSurface(
                modifier = Modifier.navigationBarsPadding(),
                state = transactionScreenState.editSurfaceState,
            )
        }
    )
}

@Preview
@Composable
private fun PreviewTransactionScreen() = AccountTheme {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()
    TransactionScreen(
        snackbarHostState = remember { SnackbarHostState() },
        transactionScreenState = TransactionScreenState(),
        onBack = { },
        pagerState = pagerState,
        onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } }
    )
}