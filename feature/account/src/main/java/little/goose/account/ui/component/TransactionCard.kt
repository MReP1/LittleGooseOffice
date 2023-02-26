package little.goose.account.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.account.R
import little.goose.account.data.constants.AccountConstant.EXPENSE
import little.goose.account.data.entities.Transaction
import little.goose.account.ui.transaction.icon.TransactionIconHelper
import little.goose.common.utils.toSignString
import little.goose.design.system.theme.AccountTheme
import java.math.BigDecimal
import java.util.*

@Composable
fun TransactionCard(
    transaction: Transaction,
    onTransactionClick: (Transaction) -> Unit
) {
    Card(
        onClick = { onTransactionClick(transaction) },
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
    ) {
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(18.dp, 0.dp, 12.dp, 0.dp),
                painter = painterResource(id = TransactionIconHelper.getIconPath(transaction.icon_id)),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            Text(
                text = transaction.content,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                modifier = Modifier.padding(20.dp, 0.dp),
                text = transaction.money.toSignString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    AccountTheme {
        TransactionCard(
            transaction = Transaction(
                null, EXPENSE, BigDecimal(71),
                "消费", "null", Date(), R.drawable.icon_book
            ), onTransactionClick = {
            }
        )
    }
}