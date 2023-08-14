package little.goose.account.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.data.models.IconDisplayType
import little.goose.account.data.models.TransactionIcon
import little.goose.account.ui.transaction.icon.TransactionIconHelper

@Composable
fun IconsBoard(
    modifier: Modifier,
    icons: List<TransactionIcon>,
    onIconClick: (TransactionIcon) -> Unit,
    selectedIcon: TransactionIcon,
    iconDisplayType: IconDisplayType
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(8.dp),
        reverseLayout = true
    ) {
        items(
            items = icons,
            key = { it.id }
        ) { transactionIcon ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .padding(8.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { onIconClick(transactionIcon) },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selectedIcon == transactionIcon)
                            MaterialTheme.colorScheme.surfaceTint
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    TransactionIcon(
                        transactionIcon = transactionIcon,
                        iconDisplayType = iconDisplayType
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.TransactionIcon(
    transactionIcon: TransactionIcon,
    iconDisplayType: IconDisplayType
) {
    when (iconDisplayType) {
        IconDisplayType.ICON_CONTENT -> {
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = transactionIcon.path),
                contentDescription = transactionIcon.name,
                modifier = Modifier
                    .size(27.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = transactionIcon.name,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(1F))
        }

        IconDisplayType.ICON_ONLY -> {
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = transactionIcon.path),
                contentDescription = transactionIcon.name,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.weight(1F))
        }
    }
}

@Preview(device = "spec:width=380dp,height=400dp,dpi=440")
@Composable
private fun PreviewIconsBoard() {
    IconsBoard(
        modifier = Modifier.fillMaxSize(),
        icons = TransactionIconHelper.expenseIconList,
        onIconClick = {},
        selectedIcon = TransactionIconHelper.expenseIconList.first(),
        iconDisplayType = IconDisplayType.ICON_CONTENT
    )
}