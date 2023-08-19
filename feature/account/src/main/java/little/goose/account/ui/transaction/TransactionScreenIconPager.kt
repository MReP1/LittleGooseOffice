package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import little.goose.account.data.models.IconDisplayType
import little.goose.account.data.models.TransactionIcon
import little.goose.account.ui.component.IconsBoard
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@Stable
internal data class TransactionScreenIconPagerState(
    val expenseSelectedIcon: TransactionIcon = TransactionIconHelper.expenseIconList[0],
    val incomeSelectedIcon: TransactionIcon = TransactionIconHelper.incomeIconList[0],
    val iconDisplayType: IconDisplayType = IconDisplayType.ICON_CONTENT,
    val onIconChangeIntent: (TransactionScreenIntent.ChangeTransaction.Icon) -> Unit = {}
)

@Composable
internal fun TransactionScreenIconPager(
    modifier: Modifier,
    pagerState: PagerState,
    state: TransactionScreenIconPagerState
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { page ->
        if (page == 0) {
            IconsBoard(
                modifier = Modifier.fillMaxSize(),
                icons = TransactionIconHelper.expenseIconList,
                onIconClick = {
                    state.onIconChangeIntent(
                        TransactionScreenIntent.ChangeTransaction.Icon(it.id, it.name)
                    )
                },
                selectedIcon = state.expenseSelectedIcon,
                iconDisplayType = state.iconDisplayType
            )
        } else {
            IconsBoard(
                modifier = Modifier.fillMaxSize(),
                icons = TransactionIconHelper.incomeIconList,
                onIconClick = {
                    state.onIconChangeIntent(
                        TransactionScreenIntent.ChangeTransaction.Icon(it.id, it.name)
                    )
                },
                selectedIcon = state.incomeSelectedIcon,
                iconDisplayType = state.iconDisplayType
            )
        }
    }
}