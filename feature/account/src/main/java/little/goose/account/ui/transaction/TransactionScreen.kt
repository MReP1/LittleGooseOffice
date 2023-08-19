package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import little.goose.account.data.constants.AccountConstant
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.component.TransactionEditSurfaceState
import little.goose.design.system.theme.AccountTheme

internal sealed class TransactionScreenState {

    data class Success(
        val pageIndex: Int = 0,
        val onChangeTransaction: (TransactionScreenIntent.ChangeTransaction) -> Unit = {},
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
    onTabSelected: (Int) -> Unit,
    transactionScreenState: TransactionScreenState,
    onBack: () -> Unit
) {
    val pagerState = if (transactionScreenState is TransactionScreenState.Success)
        rememberPagerState(initialPage = transactionScreenState.pageIndex, pageCount = { 2 })
    else rememberPagerState(initialPage = 0, pageCount = { 2 })

    LaunchedEffect(transactionScreenState) {
        (transactionScreenState as? TransactionScreenState.Success)?.let { state ->
            if (state.pageIndex != pagerState.currentPage) {
                pagerState.animateScrollToPage(state.pageIndex)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            val iconState = (transactionScreenState as? TransactionScreenState.Success)
                ?.iconPagerState ?: return@collect
            if (it == 0) {
                transactionScreenState.onChangeTransaction(
                    TransactionScreenIntent.ChangeTransaction.Icon(
                        iconState.expenseSelectedIcon.id, iconState.expenseSelectedIcon.name
                    ) + TransactionScreenIntent.ChangeTransaction.Type(
                        AccountConstant.EXPENSE
                    )
                )
            } else {
                transactionScreenState.onChangeTransaction(
                    TransactionScreenIntent.ChangeTransaction.Icon(
                        iconState.incomeSelectedIcon.id, iconState.incomeSelectedIcon.name
                    ) + TransactionScreenIntent.ChangeTransaction.Type(
                        AccountConstant.INCOME
                    )
                )
            }
        }
    }

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
                onBack = onBack,
                title = {
                    when (transactionScreenState) {
                        TransactionScreenState.Loading -> Unit
                        is TransactionScreenState.Success -> {
                            TransactionScreenTabRow(
                                modifier = Modifier.width(120.dp),
                                selectedTabIndex = pagerState.currentPage,
                                onTabSelected = onTabSelected
                            )
                        }
                    }
                },
                actions = {
                    when (transactionScreenState) {
                        TransactionScreenState.Loading -> Unit
                        is TransactionScreenState.Success -> {
                            TransactionScreenTopBarAction(
                                modifier = Modifier,
                                iconDisplayType = transactionScreenState.topBarState.iconDisplayType,
                                onIconDisplayTypeChange = transactionScreenState.topBarState.onIconDisplayTypeChange
                            )
                        }
                    }
                }
            )
        },
        content = {
            when (transactionScreenState) {
                TransactionScreenState.Loading -> {
                    // 加载时间太短了，不需要加载占位
                }

                is TransactionScreenState.Success -> {
                    TransactionScreenIconPager(
                        modifier = Modifier.padding(it),
                        pagerState = pagerState,
                        state = transactionScreenState.iconPagerState,
                    )
                }
            }
        },
        bottomBar = {
            val editSurfaceState = (transactionScreenState as? TransactionScreenState.Success)
                ?.editSurfaceState ?: remember { TransactionEditSurfaceState() }
            TransactionEditSurface(
                modifier = Modifier.navigationBarsPadding(),
                state = editSurfaceState,
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
        transactionScreenState = TransactionScreenState.Success(),
        onBack = { },
        onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } }
    )
}