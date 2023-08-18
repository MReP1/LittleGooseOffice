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
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.entities.Transaction
import little.goose.account.data.models.IconDisplayType
import little.goose.account.data.models.TransactionIcon
import little.goose.account.ui.component.TransactionEditSurface
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.design.system.theme.AccountTheme

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    transaction: Transaction,
    iconDisplayType: IconDisplayType,
    onTransactionChange: (Transaction) -> Unit,
    onIconDisplayTypeChange: (IconDisplayType) -> Unit,
    onBack: () -> Unit,
    onDoneClick: (Transaction) -> Unit,
    onAgainClick: (Transaction) -> Unit,
    onIconClick: (TransactionIcon) -> Unit,
    expenseSelectedIcon: TransactionIcon,
    incomeSelectedIcon: TransactionIcon,
    onTabSelected: (Int) -> Unit,
    pagerState: PagerState
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
                iconDisplayType = iconDisplayType,
                onIconDisplayTypeChange = onIconDisplayTypeChange,
                onBack = onBack
            )
        },
        content = {
            TransactionScreenIconPager(
                modifier = Modifier.padding(it),
                pagerState = pagerState,
                onIconClick = onIconClick,
                expenseSelectedIcon = expenseSelectedIcon,
                incomeSelectedIcon = incomeSelectedIcon,
                iconDisplayType = iconDisplayType
            )
        },
        bottomBar = {
            TransactionEditSurface(
                modifier = Modifier.navigationBarsPadding(),
                transaction = transaction,
                onTransactionChange = onTransactionChange,
                onAgainClick = onAgainClick,
                onDoneClick = onDoneClick
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
        transaction = Transaction(id = 0, type = EXPENSE, content = "饮食", icon_id = 0),
        iconDisplayType = IconDisplayType.ICON_CONTENT,
        onTransactionChange = { },
        onIconDisplayTypeChange = { },
        onBack = { },
        onAgainClick = { },
        onDoneClick = { },
        expenseSelectedIcon = TransactionIconHelper.expenseIconList[0],
        incomeSelectedIcon = TransactionIconHelper.incomeIconList[0],
        pagerState = pagerState,
        onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
        onIconClick = { }
    )
}