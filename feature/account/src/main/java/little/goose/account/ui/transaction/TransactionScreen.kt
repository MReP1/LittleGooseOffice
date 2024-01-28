package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.component.TransactionEditSurfaceState
import little.goose.design.system.theme.GooseTheme
import little.goose.ui.screen.LittleGooseLoadingScreen

internal sealed class TransactionScreenState {

    data class Success(
        val pageIndex: Int = 0,
        val topBarState: TransactionScreenTopBarState = TransactionScreenTopBarState(),
        val editSurfaceState: TransactionEditSurfaceState = TransactionEditSurfaceState(),
        val iconPagerState: TransactionScreenIconPagerState = TransactionScreenIconPagerState(),
    ) : TransactionScreenState()

    data object Loading : TransactionScreenState()
}

@Composable
internal fun TransactionScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    transactionScreenState: TransactionScreenState,
    action: (TransactionScreenIntent) -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = modifier) {
        when (transactionScreenState) {
            TransactionScreenState.Loading -> {
                LittleGooseLoadingScreen(modifier = Modifier.fillMaxSize())
            }

            is TransactionScreenState.Success -> {
                val currentTransactionScreenState by rememberUpdatedState(transactionScreenState)
                val pagerState = rememberPagerState(
                    initialPage = transactionScreenState.pageIndex,
                    pageCount = { 2 }
                )
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.drop(1).collect { currentPage ->
                        val iconState = currentTransactionScreenState.iconPagerState
                        val isExpense = currentPage == 0
                        val newIcon =
                            if (isExpense) iconState.expenseSelectedIcon else iconState.incomeSelectedIcon
                        val changeIcon = TransactionScreenIntent.ChangeTransaction.Icon(newIcon)
                        val changeType = TransactionScreenIntent.ChangeTransaction.Type(
                            if (isExpense) AccountConstant.EXPENSE else AccountConstant.INCOME
                        )
                        action(changeIcon + changeType)
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
                            onBack = onBack,
                            title = {
                                val scope = rememberCoroutineScope()
                                TransactionScreenTabRow(
                                    modifier = Modifier.width(120.dp),
                                    selectedTabIndex = pagerState.currentPage,
                                    offsetFraction = pagerState.currentPageOffsetFraction,
                                    onTabSelected = { index ->
                                        scope.launch { pagerState.animateScrollToPage(index) }
                                    }
                                )
                            },
                            actions = {
                                TransactionScreenTopBarAction(
                                    modifier = Modifier,
                                    iconDisplayType = transactionScreenState.topBarState.iconDisplayType,
                                    onIconDisplayTypeChange = action
                                )
                            }
                        )
                    },
                    content = {
                        TransactionScreenIconPager(
                            modifier = Modifier.padding(it),
                            pagerState = pagerState,
                            state = transactionScreenState.iconPagerState,
                            onIconChangeIntent = action
                        )
                    },
                    bottomBar = {
                        TransactionEditSurface(
                            modifier = Modifier.navigationBarsPadding(),
                            state = transactionScreenState.editSurfaceState,
                            onTransactionChangeIntent = action,
                            onOperationIntent = action
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewTransactionScreen() = GooseTheme {
    TransactionScreen(
        snackbarHostState = remember { SnackbarHostState() },
        transactionScreenState = TransactionScreenState.Success(),
        action = { },
        onBack = { }
    )
}