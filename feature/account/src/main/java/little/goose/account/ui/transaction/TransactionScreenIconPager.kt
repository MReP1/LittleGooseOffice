package little.goose.account.ui.transaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.account.data.models.IconDisplayType
import little.goose.account.data.models.TransactionIcon
import little.goose.account.ui.component.IconsBoard
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@Composable
internal fun TransactionScreenIconPager(
    modifier: Modifier,
    pagerState: PagerState,
    onIconClick: (TransactionIcon) -> Unit,
    expenseSelectedIcon: TransactionIcon,
    incomeSelectedIcon: TransactionIcon,
    iconDisplayType: IconDisplayType,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { page ->
        if (page == 0) {
            IconsBoard(
                modifier = Modifier.fillMaxSize(),
                icons = TransactionIconHelper.expenseIconList,
                onIconClick = onIconClick,
                selectedIcon = expenseSelectedIcon,
                iconDisplayType = iconDisplayType
            )
        } else {
            IconsBoard(
                modifier = Modifier.fillMaxSize(),
                icons = TransactionIconHelper.incomeIconList,
                onIconClick = onIconClick,
                selectedIcon = incomeSelectedIcon,
                iconDisplayType = iconDisplayType
            )
        }
    }
}