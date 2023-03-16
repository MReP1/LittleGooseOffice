package little.goose.account.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.entities.Transaction

@Composable
fun TransactionColumn(
    modifier: Modifier,
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = transactions,
            key = { it.id ?: it }
        ) { transaction ->
            if (transaction.type == AccountConstant.TIME) {
                Text(text = transaction.description)
            } else {
                TransactionCard(
                    transaction = transaction,
                    onTransactionClick = onTransactionClick
                )
            }
        }
    }
}