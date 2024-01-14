package little.goose.account.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.entities.Transaction
import little.goose.design.system.component.dialog.DeleteDialog
import little.goose.design.system.component.dialog.DeleteDialogState
import little.goose.design.system.theme.GooseTheme
import little.goose.design.system.theme.LocalWindowSizeClass
import java.math.BigDecimal

@Stable
data class TransactionColumnState(
    val transactions: List<Transaction> = emptyList(),
    val isMultiSelecting: Boolean = false,
    val multiSelectedTransactions: Set<Transaction> = emptySet(),
    val onTransactionSelected: (item: Transaction, selected: Boolean) -> Unit = { _, _ -> },
    val selectAllTransactions: () -> Unit = {},
    val cancelMultiSelecting: () -> Unit = {},
    val deleteTransactions: (List<Transaction>) -> Unit = {}
)

@Composable
fun TransactionColumn(
    modifier: Modifier,
    state: TransactionColumnState,
    title: (@Composable LazyGridItemScope.() -> Unit)? = null,
    monthSelector: (@Composable LazyGridItemScope.() -> Unit)? = null,
    onTransactionEdit: (Transaction) -> Unit
) {
    val deleteDialogState = remember { DeleteDialogState() }
    val windowSizeClass = LocalWindowSizeClass.current
    val fixedCount = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }
    LazyVerticalGrid(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 16.dp),
        columns = GridCells.Fixed(fixedCount),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        title?.let {
            item(span = { GridItemSpan(fixedCount) }, content = it)
        }
        monthSelector?.let {
            item(span = { GridItemSpan(fixedCount) }, content = it)
        }
        items(
            items = state.transactions,
            key = { it.id ?: it.toString() },
            span = { transaction ->
                if (transaction.type == AccountConstant.TIME) {
                    GridItemSpan(fixedCount)
                } else {
                    GridItemSpan(1)
                }
            }
        ) { transaction ->
            if (transaction.type == AccountConstant.TIME) {
                Text(
                    text = transaction.description,
                    modifier = Modifier
                )
            } else {
                var isExpended by remember { mutableStateOf(false) }
                TransactionCard(
                    modifier = Modifier,
                    transaction = transaction,
                    isExpended = isExpended,
                    isMultiSelecting = state.isMultiSelecting,
                    selected = state.multiSelectedTransactions.contains(transaction),
                    onTransactionClick = { isExpended = !isExpended },
                    onTransactionEdit = onTransactionEdit,
                    onTransactionDelete = {
                        deleteDialogState.show {
                            state.deleteTransactions(listOf(it))
                        }
                    },
                    onSelectTransaction = state.onTransactionSelected
                )
            }
        }
    }

    DeleteDialog(state = deleteDialogState)
}

@Preview
@Composable
fun PreviewTransactionColumn() = GooseTheme {
    TransactionColumn(
        modifier = Modifier.fillMaxSize(),
        state = TransactionColumnState(
            transactions = List(10) {
                Transaction(
                    id = it.toLong(), type = EXPENSE, money = BigDecimal(10)
                )
            }
        ),
        onTransactionEdit = {}
    )
}